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

package com.test.yukthitech.webutils.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.IEmployeeController;
import com.test.yukthitech.webutils.models.TestEmpSearchQuery;
import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.webutils.client.helpers.SearchHelper;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.LovType;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.ILovController;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.ValueLabel;
import com.yukthitech.webutils.common.models.search.ExecuteSearchResponse;
import com.yukthitech.webutils.common.models.search.SearchRow;

/**
 * Tests the data save and fetch are restricted to spaces.
 * @author akiran
 */
public class TFUserSpace extends TFBase
{
	private IEmployeeController employeeController;
	private ILovController lovController;
	private SearchHelper searchHelper = new SearchHelper();
	
	private IRequestCustomizer requestCustomizer1;
	private IRequestCustomizer requestCustomizer2;
	
	@BeforeClass
	public void setup()
	{
		employeeController = super.clientControllerFactory.getController(IEmployeeController.class);
		lovController = super.clientControllerFactory.getController(ILovController.class);
		
		requestCustomizer1 = new IRequestCustomizer()
		{
			@Override
			public void customize(RestRequest<?> request)
			{
				request.addHeader("customerId", "1");
			}
		};
		
		requestCustomizer2 = new IRequestCustomizer()
		{
			@Override
			public void customize(RestRequest<?> request)
			{
				request.addHeader("customerId", "2");
			}
		};
	}
	
	private long addEmployee(String name, IRequestCustomizer customizer)
	{
		TestEmployeeModel emp = new TestEmployeeModel(name, 1000);
		BasicSaveResponse response = employeeController.setRequestCustomizer(customizer).save(emp);
	
		return response.getId();
	}
	
	private Set<String> getLovList(IRequestCustomizer customizer)
	{
		List<ValueLabel> lovList = lovController.setRequestCustomizer(customizer).fetchLov("employeeLov", LovType.DYNAMIC_TYPE).getLovList();
		
		if(lovList == null || lovList.isEmpty())
		{
			return null;
		}
		
		Set<String> names = new HashSet<>();
		
		for(ValueLabel vl : lovList)
		{
			names.add(vl.getLabel());
		}
		
		return names;
	}

	/**
	 * Save employee objects in different space. And ensure employee object created in one space is 
	 * not accessible in other space.
	 */
	@Test
	public void testSave()
	{
		TestEmployeeModel emp = new TestEmployeeModel("TestEmp", 1000);
		BasicSaveResponse response = employeeController.setRequestCustomizer(requestCustomizer1).save(emp);
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);

		//Validate entity is accessible in same space
		BasicReadResponse<TestEmployeeModel> fetchRes1 = employeeController.setRequestCustomizer(requestCustomizer1).fetch(response.getId());
		Assert.assertNotNull(fetchRes1.getModel());
		
		//Validate entity is accessible in same space
		BasicReadResponse<TestEmployeeModel> fetchRes2 = employeeController.setRequestCustomizer(requestCustomizer2).fetch(response.getId());
		Assert.assertNull(fetchRes2.getModel());
	}
	
	/**
	 * Save different employee objects in different spaces. Ensure lov fetch is limited to single space.
	 */
	@Test
	public void testLov()
	{
		employeeController.deleteAll();
		
		//add test data
		addEmployee("abc1", requestCustomizer1);
		addEmployee("abc2", requestCustomizer1);
		addEmployee("abc3", requestCustomizer1);
		
		addEmployee("xyz1", requestCustomizer2);
		addEmployee("xyz2", requestCustomizer2);
		addEmployee("xyz3", requestCustomizer2);
		
		//test lov data
		Assert.assertEquals(getLovList(requestCustomizer1), CommonUtils.toSet("abc1", "abc2", "abc3"));
		Assert.assertEquals(getLovList(requestCustomizer2), CommonUtils.toSet("xyz1", "xyz2", "xyz3"));
		
		Assert.assertNull(getLovList(null));
	}

	private Set<String> getSearch(IRequestCustomizer customizer)
	{
		clientContext.setRequestCustomizer(customizer);
		
		TestEmpSearchQuery query = new TestEmpSearchQuery("*");
		ExecuteSearchResponse results = searchHelper.executeSearchQuery(clientContext, "empSearch", query, -1, -1);

		if(results.getSearchResults() == null || results.getSearchResults().isEmpty())
		{
			return null;
		}
		
		Set<String> names = new HashSet<>();
		
		for(SearchRow vl : results.getSearchResults())
		{
			names.add(vl.getData().get(1));
		}
		
		return names;
	}

	/**
	 * Save different employee objects in different spaces. Ensure search fetch is limited to single space.
	 */
	@Test
	public void testSearch()
	{
		employeeController.deleteAll();
		
		//add test data
		addEmployee("abc1", requestCustomizer1);
		addEmployee("abc2", requestCustomizer1);
		addEmployee("abc3", requestCustomizer1);
		
		addEmployee("xyz1", requestCustomizer2);
		addEmployee("xyz2", requestCustomizer2);
		addEmployee("xyz3", requestCustomizer2);
		
		//test lov data
		Assert.assertEquals(getSearch(requestCustomizer1), CommonUtils.toSet("abc1", "abc2", "abc3"));
		Assert.assertEquals(getSearch(requestCustomizer2), CommonUtils.toSet("xyz1", "xyz2", "xyz3"));
		
		Assert.assertNull(getLovList(null));
	}

	@AfterClass
	private void clean()
	{
		employeeController.deleteAll();
	}
}
