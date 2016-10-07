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

package com.test.yukthi.webutils.services;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthi.webutils.Authorization;
import com.test.yukthi.webutils.SecurityRole;
import com.test.yukthi.webutils.TestUserDetails;
import com.yukthi.utils.CommonUtils;
import com.yukthi.webutils.common.models.ActiveUserModel;
import com.yukthi.webutils.extensions.ExtensionEntityDetails;
import com.yukthi.webutils.repository.UserEntity;
import com.yukthi.webutils.repository.file.FileEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.security.UserDetails;
import com.yukthi.webutils.services.CurrentUserService;

/**
 * @author akiran
 *
 */
@Service
public class TestAuthenticationService implements ISecurityService
{
	/** The user service. */
	@Autowired
	private TestUserService userService;
	
	/** The current user service. */
	@Autowired
	private CurrentUserService currentUserService;
	
	@Autowired
	private HttpServletRequest request;
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#authenticate(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public TestUserDetails authenticate(String userName, String password, Map<String, String> attrMap)
	{
		if(!"admin".equals(userName) || !"admin".equals(password))
		{
			return null;
		}
		
		return new TestUserDetails(userService.getUserId(), CommonUtils.toSet(SecurityRole.ADMIN, SecurityRole.CLIENT_ADMIN), 4321L);
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#isAuthorized(com.yukthi.webutils.security.UserDetails, java.lang.reflect.Method)
	 */
	@Override
	public boolean isAuthorized(Method method)
	{
		Authorization authorization = method.getAnnotation(Authorization.class);
		
		//if target method is not secured, return true
		if(authorization == null)
		{
			return true;
		}
		
		//check if current user has at least one role from required roles, if found return true 
		Set<SecurityRole> userRoles = ((TestUserDetails)currentUserService.getCurrentUserDetails()).getRoles();
		
		for(SecurityRole role : authorization.value())
		{
			if(userRoles.contains(role))
			{
				return true;
			}
		}
		
		//if user does not have any of required roles
		return false;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#isExtensionAuthorized(com.yukthi.webutils.security.UserDetails, com.yukthi.webutils.extensions.ExtensionPointDetails)
	 */
	@Override
	public boolean isExtensionAuthorized(ExtensionEntityDetails extensionPoint)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#addSecurityCustomization(com.yukthi.webutils.repository.file.FileEntity)
	 */
	@Override
	public void addSecurityCustomization(FileEntity fileEntity)
	{
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#isAuthorized(com.yukthi.webutils.repository.file.FileEntity)
	 */
	@Override
	public boolean isAuthorized(FileEntity fileEntity)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.security.ISecurityService#getActiverUser()
	 */
	@Override
	public ActiveUserModel getActiverUser()
	{
		return new ActiveUserModel();
	}

	@Override
	public String getUserSpaceIdentity()
	{
		String custId = request.getHeader("customerId");
		return (custId != null && custId.trim().length() > 0) ? "Cust-" + custId : "admin";
	}

	@Override
	public UserDetails getUserDetailsFor(UserEntity userEntity)
	{
		return null;
	}
}
