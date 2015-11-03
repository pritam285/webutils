/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.services;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.yukthi.webutils.IDynamicRepositoryMethodRegistry;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.LovQuery;
import com.yukthi.webutils.common.annotations.Label;
import com.yukthi.webutils.common.models.ValueLabel;
import com.yukthi.webutils.services.dynamic.DynamicMethod;


/**
 * Service to fetch LOV values
 * @author akiran
 */
@Service
public class LovService implements IDynamicRepositoryMethodRegistry<LovQuery>
{
	/**
	 * Message source to fetch ENUM field labels
	 */
	@Autowired
	private MessageSource messageSource;
	
	private Map<String, DynamicMethod> nameToLovMet = new HashMap<>();
	
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IDynamicRepositoryMethodRegistry#registerDynamicRepositoryMethod(com.yukthi.webutils.services.dynamic.DynamicMethod, java.lang.annotation.Annotation)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void registerDynamicRepositoryMethod(DynamicMethod method, LovQuery annotation)
	{
		Type returnType = method.getMethod().getGenericReturnType();
		
		if(!(returnType instanceof ParameterizedType))
		{
			throw new IllegalStateException("Invalid return type specified for lov method - " + method);
		}
		
		ParameterizedType parameterizedType = (ParameterizedType)returnType;
		
		if( !Collection.class.isAssignableFrom((Class)parameterizedType.getRawType()) || 
				!ValueLabel.class.equals(parameterizedType.getActualTypeArguments()[0]) )
		{
			throw new IllegalStateException("Invalid return type specified for lov method - " + method);
		}
		
		nameToLovMet.put(annotation.value(), method);
	}

	/**
	 * Fetches specified enum fields as {@link ValueLabel} list
	 * @param name Enum class name
	 * @param locale Local in which LOV needs to be fetched
	 * @return List of LOVs as {@link ValueLabel}
	 */
	@Cacheable
	@SuppressWarnings("rawtypes")
	public List<ValueLabel> getEnumLovValues(String name, Locale locale)
	{
		try
		{
			Class<?> enumType = Class.forName(name);
			
			//if invalid enum is specified
			if(!enumType.isEnum())
			{
				throw new InvalidRequestParameterException("Invalid enum type specified: " + name);
			}

			Object enumValues[] = enumType.getEnumConstants();
			List<ValueLabel> valueLst = new ArrayList<>();
			Enum<?> enumObj = null;
			String label = null;
			Label labelAnnot = null;
			Field field = null;
			
			//loop through enum fields
			for(Object obj: enumValues)
			{
				enumObj = (Enum)obj;
				
				//fetch enum field
				try
				{
					field = enumType.getField(enumObj.name());
				}catch(NoSuchFieldException | SecurityException e)
				{
					//ignore, this should never happen
					e.printStackTrace();
				}
				
				//Fetch the label for current enum field
				
				//try to fetch from message source using <enum-name>.<field-name>.label
				label = getMessage(enumType.getName() + "." + enumObj.name() + ".label", locale);
				
				//if not found
				if(label == null)
				{
					//try to fetch from message source using <field-name>.label
					label = getMessage(enumObj.name() + ".label", locale);
				}
				
				//if not found and @Label is defined on enum field
				if(label == null && (labelAnnot = field.getAnnotation(Label.class)) != null)
				{
					//get @Label value
					label = labelAnnot.value();
				}
				
				//if label can not be found in any means
				if(label == null)
				{
					//use field name
					label = enumObj.name();
				}
				
				valueLst.add(new ValueLabel(enumObj.name(), label));
			}
			
			return valueLst;
		}catch(ClassNotFoundException ex)
		{
			throw new InvalidRequestParameterException("Failed to fetch enum LOV for specified type: " + name, ex);
		}
	}
	
	/**
	 * Tries to fetch message with specified key, if not found returns null
	 * @param key Key for which message needs to be fetched
	 * @param locale
	 * @return Message matching with the key
	 */
	private String getMessage(String key, Locale locale)
	{
		try
		{
			return messageSource.getMessage(key, null, locale);
		}catch(Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Fetches dynamic LOV values based on the specified lov name
	 * @param name Lov name
	 * @param locale Locale in which values needs to be fetched. Current this is not used
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public List<ValueLabel> getDynamicLovValues(String name, Locale locale)
	{
		DynamicMethod method = nameToLovMet.get(name);
		
		if(method == null)
		{
			throw new InvalidParameterException("Invalid LOV name specified - " + name);
		}
		
		return (List<ValueLabel>)method.invoke();
	}
	
	/**
	 * Checks if the specified name is valid dynamic lov name
	 * @param name Name to be validated
	 * @return True, if specified name is valid dynamic lov name
	 */
	public boolean isValidDynamicLov(String name)
	{
		return nameToLovMet.containsKey(name);
	}
}