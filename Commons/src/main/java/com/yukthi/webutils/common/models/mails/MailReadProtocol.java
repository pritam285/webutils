package com.yukthi.webutils.common.models.mails;

/**
 * Enumeration of supported mail read protocol.
 * @author akiran
 */
public enum MailReadProtocol
{
	/**
	 * IMAPS read protocol.
	 */
	IMAPS("imaps"),
	
	/**
	 * POP3 read protocol.
	 */
	POP3("pop3");
	
	/**
	 * String name of the protocol.
	 */
	private String name;

	/**
	 * Instantiates a new mail read protocol.
	 *
	 * @param name the name
	 */
	private MailReadProtocol(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the string name of the protocol.
	 *
	 * @return the string name of the protocol
	 */
	public String getName()
	{
		return name;
	}
}
