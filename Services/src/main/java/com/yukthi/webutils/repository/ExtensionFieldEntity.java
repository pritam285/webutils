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

package com.yukthi.webutils.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.DeleteWithParent;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;
import com.yukthi.persistence.conversion.impl.JsonConverter;
import com.yukthi.webutils.common.extensions.ExtensionFieldType;
import com.yukthi.webutils.common.extensions.LovOption;

/**
 * Represents extension field entity
 * 
 * @author akiran
 */
@Table(name = "EXTENSION_FIELDS")
@UniqueConstraints({
	@UniqueConstraint(name = "EXT_ID_NAME", fields = {"extension", "name"})
})
public class ExtensionFieldEntity
{
	/**
	 * Primary key of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	/**
	 * Extension for which this field is being defined
	 */
	@ManyToOne
	@Column(name = "EXTENSION_ID", nullable = false)
	@DeleteWithParent
	private ExtensionEntity extension;

	/**
	 * Name of the field
	 */
	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

	/**
	 * Description of the field
	 */
	@Column(name = "DESCRIPTION", length = 500)
	private String description;

	/**
	 * Field type
	 */
	@Column(name = "DATA_TYPE", length = 50)
	@DataTypeMapping(type = DataType.STRING)
	private ExtensionFieldType type;

	/**
	 * Indicates whether this field is mandatory field
	 */
	@Column(name = "IS_REQUIRED")
	private boolean required = false;

	/**
	 * LOV options for fields of LOV type.
	 */
	@Column(name = "LOV_OPTIONS", length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private List<LovOption> lovOptions;

	/**
	 * Internal field to be used to fetch unique set of lov values
	 */
	@Transient
	private Set<String> lovValues;
	
	/**
	 * Instantiates a new extension field entity.
	 */
	public ExtensionFieldEntity()
	{}
	
	/**
	 * Instantiates a new extension field entity.
	 *
	 * @param id the id
	 */
	public ExtensionFieldEntity(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id
	 *            the new primary key of the entity
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the extension for which this field is being defined.
	 *
	 * @return the extension for which this field is being defined
	 */
	public ExtensionEntity getExtension()
	{
		return extension;
	}

	/**
	 * Sets the extension for which this field is being defined.
	 *
	 * @param extension
	 *            the new extension for which this field is being defined
	 */
	public void setExtension(ExtensionEntity extension)
	{
		this.extension = extension;
	}

	/**
	 * Gets the name of the field.
	 *
	 * @return the name of the field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the field.
	 *
	 * @param name
	 *            the new name of the field
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the field.
	 *
	 * @return the description of the field
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the field.
	 *
	 * @param description
	 *            the new description of the field
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the field type.
	 *
	 * @return the field type
	 */
	public ExtensionFieldType getType()
	{
		return type;
	}

	/**
	 * Sets the field type.
	 *
	 * @param type
	 *            the new field type
	 */
	public void setType(ExtensionFieldType type)
	{
		this.type = type;
	}

	/**
	 * Checks if is indicates whether this field is mandatory field.
	 *
	 * @return the indicates whether this field is mandatory field
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the indicates whether this field is mandatory field.
	 *
	 * @param required
	 *            the new indicates whether this field is mandatory field
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}

	/**
	 * Gets the lOV options for fields of LOV type.
	 *
	 * @return the lOV options for fields of LOV type
	 */
	public List<LovOption> getLovOptions()
	{
		return lovOptions;
	}

	/**
	 * Sets the lOV options for fields of LOV type.
	 *
	 * @param lovOptions the new lOV options for fields of LOV type
	 */
	public void setLovOptions(List<LovOption> lovOptions)
	{
		this.lovOptions = lovOptions;
	}

	/**
	 * @return the {@link #lovValues lovValues}
	 */
	public Set<String> getLovValues()
	{
		if(type != ExtensionFieldType.LIST_OF_VALUES)
		{
			return null;
		}
		
		if(lovValues != null)
		{
			return lovValues;
		}
		
		Set<String> lovValues = new HashSet<>();
		
		for(LovOption option : this.lovOptions)
		{
			lovValues.add(option.getValue());
		}
		
		this.lovValues = lovValues;
		return lovValues;
	}
}