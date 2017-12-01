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

package com.yukthitech.webutils.annotations;

import static com.yukthitech.webutils.IWebUtilsInternalConstants.EXT_FIELD_COUNT;
import static com.yukthitech.webutils.IWebUtilsInternalConstants.EXT_FIELD_PREFIX;
import static com.yukthitech.webutils.IWebUtilsInternalConstants.MAX_EXT_FIELD_LENGTH;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.persistence.annotations.Extendable;
import com.yukthitech.persistence.repository.annotations.Charset;

/**
 * Used to mark an entity as extendable and specifies name for ui display.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extendable(fieldSize = MAX_EXT_FIELD_LENGTH, count = EXT_FIELD_COUNT, fieldPrefix = EXT_FIELD_PREFIX, charset = Charset.LATIN1)
public @interface ExtendableEntity
{
	/**
	 * Name of the extension entity (to display in ui).
	 * @return Name of the extension entity
	 */
	public String name();
}
