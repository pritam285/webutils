package com.yukthitech.webutils.common.action.mobile;

import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Displays editable sms message which can be updated by user, post which it 
 * can be sent to target user.
 * @author akiran
 */
public class EditAndSendSmsAction implements IAgentAction
{
	/**
	 * Label to be used for action.
	 */
	private String label;

	/**
	 * Sms message being sent.
	 */
	private String message;
	
	/**
	 * Number to which sms message needs to be sent.
	 */
	private String number;
	
	/**
	 * Gets the label to be used for action.
	 *
	 * @return the label to be used for action
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label to be used for action.
	 *
	 * @param label the new label to be used for action
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the sms message being sent.
	 *
	 * @return the sms message being sent
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the sms message being sent.
	 *
	 * @param message the new sms message being sent
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the number to which sms message needs to be sent.
	 *
	 * @return the number to which sms message needs to be sent
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Sets the number to which sms message needs to be sent.
	 *
	 * @param number the new number to which sms message needs to be sent
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}
}
