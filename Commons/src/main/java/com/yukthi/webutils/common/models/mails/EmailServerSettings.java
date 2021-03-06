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

package com.yukthi.webutils.common.models.mails;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

/**
 * Settings or information required for sending and reading the mails.
 * @author akiran
 */
public class EmailServerSettings
{
	/**
	 * Property name for setting smtp host.
	 */
	public static final String PROP_SMTP_HOST = "mail.smtp.host";
	
	/**
	 * Property for setting smtp port.
	 */
	public static final String PROP_SMTP_PORT = "mail.smtp.port";
	
	/**
	 * Property indicating if auth is enabled or not.
	 */
	public static final String PROP_USE_AUTH = "mail.smtp.auth";
	
	/**
	 * Property indicating if TTLS should be used.
	 */
	public static final String PROP_ENABLE_TTLS = "mail.smtp.starttls.enable";

	/**
	* Smtp host.
	*/
	private String smtpHost;
	
	/**
	 * Smtp port.
	 */
	private Integer smtpPort;

	/**
	 * Flag to indicate whether authentication to be used.
	 */
	private boolean useAuthentication = false;
	
	/**
	 * User name for authentication.
	 */
	private String userName;
	
	/**
	 * Password for authentication.
	 */
	private String password;

	/**
	 * Flag indicating if ttls should be enabled.
	 */
	private boolean enableTtls = false;
	
	/**
	 * Protocol to be used for reading mails.
	 */
	private MailReadProtocol readProtocol;
	
	/**
	 * Host address from where mail can be read or deleted.
	 */
	private String readHost;
	
	/**
	 * Folders from which mails needs to be accessed.
	 */
	private List<String> folderNames = Arrays.asList("INBOX");
	
	/**
	 * Gets the smtp host.
	 *
	 * @return the smtp host
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * Sets the smtp host.
	 *
	 * @param smtpHost the new smtp host
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	/**
	 * Gets the smtp port.
	 *
	 * @return the smtp port
	 */
	public Integer getSmtpPort()
	{
		return smtpPort;
	}

	/**
	 * Sets the smtp port.
	 *
	 * @param smtpPort the new smtp port
	 */
	public void setSmtpPort(Integer smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/**
	 * Checks if is flag to indicate whether authentication to be used.
	 *
	 * @return the flag to indicate whether authentication to be used
	 */
	public boolean isUseAuthentication()
	{
		return useAuthentication;
	}

	/**
	 * Sets the flag to indicate whether authentication to be used.
	 *
	 * @param useAuthentication the new flag to indicate whether authentication to be used
	 */
	public void setUseAuthentication(boolean useAuthentication)
	{
		this.useAuthentication = useAuthentication;
	}

	/**
	 * Gets the user name for authentication.
	 *
	 * @return the user name for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name for authentication.
	 *
	 * @param userName the new user name for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password for authentication.
	 *
	 * @return the password for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password for authentication.
	 *
	 * @param password the new password for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Checks if is flag indicating if ttls should be enabled.
	 *
	 * @return the flag indicating if ttls should be enabled
	 */
	public boolean isEnableTtls()
	{
		return enableTtls;
	}

	/**
	 * Sets the flag indicating if ttls should be enabled.
	 *
	 * @param enableTtls the new flag indicating if ttls should be enabled
	 */
	public void setEnableTtls(boolean enableTtls)
	{
		this.enableTtls = enableTtls;
	}
	
	/**
	 * Gets the protocol to be used for reading mails.
	 *
	 * @return the protocol to be used for reading mails
	 */
	public MailReadProtocol getReadProtocol()
	{
		return readProtocol;
	}

	/**
	 * Sets the protocol to be used for reading mails.
	 *
	 * @param readProtocol the new protocol to be used for reading mails
	 */
	public void setReadProtocol(MailReadProtocol readProtocol)
	{
		this.readProtocol = readProtocol;
	}

	/**
	 * Gets the host address from where mail can be read or deleted.
	 *
	 * @return the host address from where mail can be read or deleted
	 */
	public String getReadHost()
	{
		return readHost;
	}

	/**
	 * Sets the host address from where mail can be read or deleted.
	 *
	 * @param readHost the new host address from where mail can be read or deleted
	 */
	public void setReadHost(String readHost)
	{
		this.readHost = readHost;
	}

	/**
	 * Gets the folders from which mails needs to be accessed.
	 *
	 * @return the folders from which mails needs to be accessed
	 */
	public List<String> getFolderNames()
	{
		return folderNames;
	}

	/**
	 * Sets the folders from which mails needs to be accessed.
	 *
	 * @param folderNames the new folders from which mails needs to be accessed
	 */
	public void setFolderNames(List<String> folderNames)
	{
		this.folderNames = folderNames;
	}

	/**
	* Validates required configuration params are provided.
	*/
	@PostConstruct
	private void validate()
	{
		if(StringUtils.isEmpty(smtpHost))
		{
			throw new IllegalStateException("No SMTP host is provided");
		}

		if(useAuthentication)
		{
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))
			{
				throw new IllegalStateException("No username/password is provided");
			}
		}
	}

	/**
	* Converts this configuration into properties compatible with java-mail.
	*
	* @return Java mail compatible properties.
	*/
	public Properties toProperties()
	{
		Properties props = new Properties();
		
		if(useAuthentication)
		{
			props.put(PROP_USE_AUTH, "true");
		}
		
		if(enableTtls)
		{
			props.put(PROP_ENABLE_TTLS, "true");
		}
		
		props.put(PROP_SMTP_HOST, smtpHost);
		
		if(smtpPort != null)
		{
			props.put(PROP_SMTP_PORT, "" + smtpPort);
		}
		
		return props;
	}
}
