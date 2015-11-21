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

package com.yukthi.webutils.security;

import java.lang.reflect.Method;

/**
 * Authentication service to be provided by the webapplication to authenticate and authorize
 * the users.
 * @author akiran
 */
public interface ISecurityService
{
	/**
	 * Authenticates the specified user name and password and returns user details, if inputs
	 * are value
	 * @param userName User name
	 * @param password password
	 * @return User details if authentication is successful, otherwise null
	 */
	public UserDetails authenticate(String userName, String password);
	
	/**
	 * Invoked to check if specified user is authorized to invoke specified method. This method is expected to read
	 * security annotations from the target method and cross check with specified roles and decide the authorization
	 * @param userDetails Current user details who is trying to invoke target method
	 * @param method Method being invoked
	 * @return True, if user is authorized to invoke the method
	 */
	public boolean isAuthorized(UserDetails userDetails, Method method);
}