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

package com.yukthi.webutils.common.models;

/**
 * Generic count response
 * 
 * @author akiran
 */
public class BasicCountResponse extends BaseResponse
{
	/**
	 * ID of the saved entity
	 */
	private long count;

	/**
	 * Instantiates a new login response.
	 */
	public BasicCountResponse()
	{}

	/**
	 * Instantiates a new basic count response.
	 *
	 * @param count
	 *            the count
	 */
	public BasicCountResponse(long count)
	{
		this.count = count;
	}

	/**
	 * Gets the iD of the saved entity.
	 *
	 * @return the iD of the saved entity
	 */
	public long getCount()
	{
		return count;
	}

	/**
	 * Sets the iD of the saved entity.
	 *
	 * @param count
	 *            the new iD of the saved entity
	 */
	public void setCount(long count)
	{
		this.count = count;
	}
}
