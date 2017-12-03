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

package com.test.yukthitech.webutils.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.yukthitech.webutils.models.IEmployeeController;
import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.test.yukthitech.webutils.services.EmployeeService;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicCountResponse;
import com.yukthitech.webutils.controllers.BaseCrudController;

/**
 * Test controller to test spring validation enablement.
 * @author akiran
 */
@RestController
@RequestMapping("/employee")
@ActionName("employee")
public class EmployeeController extends BaseCrudController<TestEmployeeModel, EmployeeService> implements IEmployeeController
{
	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.IEmployeeController#deleteAll()
	 */
	@Override
	@ResponseBody
	@RequestMapping("/deleteAll")
	@ActionName("deleteAll")
	public BaseResponse deleteAll()
	{
		getService().deleteAll();
		return new BaseResponse();
	}

	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.IEmployeeController#count()
	 */
	@Override
	@ResponseBody
	@RequestMapping("/count")
	@ActionName("count")
	public BasicCountResponse count()
	{
		return new BasicCountResponse(getService().getCount());
	}
}
