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

package com.yukthi.webutils.controllers;

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_FILES;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_ATTACHMENT;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_ID;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.FileInfo;
import com.yukthi.webutils.services.FileService;
import com.yukthi.webutils.utils.WebAttachmentUtils;

/**
 * Controller for fetching files
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_FILES)
@RequestMapping("/files")
public class FileController
{
	@Autowired
	private FileService fileService;
	
	/**
	 * Fetches file content from db for specified id, as part of request body. Useful to include content as
	 * image, css etc
	 * @param id Id of the file to be fetched
	 * @param response Response on which content needs to be sent
	 * @throws IOException
	 */
	@ActionName(ACTION_TYPE_FETCH)
	@RequestMapping(value = "/fetch/{" + PARAM_ID + "}", method = RequestMethod.POST)
	public void fetchFile(@PathVariable(PARAM_ID) long id, HttpServletResponse response) throws IOException
	{
		FileInfo fileInfo = fileService.getFileInfo(id);
		
		if(fileInfo == null)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		WebAttachmentUtils.sendFile(response, fileInfo, false);
	}

	/**
	 * Fetches file content from db for specified id, as part attachment.
	 * @param id Id of the file to be fetched
	 * @param response Response on which content needs to be sent
	 * @throws IOException
	 */
	@ActionName(ACTION_TYPE_FETCH_ATTACHMENT)
	@RequestMapping(value = "/download/{" + PARAM_ID + "}", method = RequestMethod.POST)
	public void fetchFileAsAttachment(@PathVariable(PARAM_ID) long id, HttpServletResponse response) throws IOException
	{
		FileInfo fileInfo = fileService.getFileInfo(id);
		
		if(fileInfo == null)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		WebAttachmentUtils.sendFile(response, fileInfo, true);
	}
}