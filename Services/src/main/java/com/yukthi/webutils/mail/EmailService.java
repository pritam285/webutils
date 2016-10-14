package com.yukthi.webutils.mail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.utils.ReflectionUtils;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.models.mails.EmailServerSettings;
import com.yukthi.webutils.common.models.mails.MailTemplateConfiguration;
import com.yukthi.webutils.mail.template.MailTemplateConfigService;
import com.yukthi.webutils.services.FreeMarkerService;

/**
 * Service to send and receive mails.
 * 
 * @author akiran
 */
@Service
public class EmailService
{
	private static Logger logger = LogManager.getLogger(EmailService.class);
	
	/**
	 * Mail header name which indicates the mail part is generated by webutils.
	 */
	private static final String HEADER_NAME_WEBUTILS = "com.yukthi.webutils.mail.EmailService.header.default";
	
	/**
	 * Mail template configuration service, used to fetch meta information from context.
	 */
	@Autowired
	private MailTemplateConfigService mailTemplateConfigService;
	
	/**
	 * Freemarker service to process email templates.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;
	
	/**
	 * Create new java mail session with the configuration provided to the
	 * service.
	 *
	 * @param settings Settings to create session.
	 * @return newly created session.
	 */
	private Session newSession(EmailServerSettings settings)
	{
		Properties configProperties = settings.toProperties();
		Session mailSession = null;

		//if authentication needs to be done provide user name and password
		if(settings.isUseAuthentication())
		{
			mailSession = Session.getInstance(configProperties, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(settings.getUserName(), settings.getPassword());
				}
			});
		}
		else
		{
			mailSession = Session.getInstance(configProperties);
		}

		return mailSession;
	}

	/**
	 * Converts provided list of email string to InternetAddress objects.
	 * 
	 * @param listName List name for which conversion is being done (Eg: TO, CC, etc).
	 * @param ids Ids to be converted.
	 * @return Converted Internet address list.
	 */
	private InternetAddress[] convertToInternetAddress(String listName, String ids[]) throws AddressException
	{
		InternetAddress[] res = new InternetAddress[ids.length];

		for(int i = 0; i < ids.length; i++)
		{
			try
			{
				res[i] = new InternetAddress(ids[i]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException(ex, "An error occurred while parsing email-id {} from {} list", ids[i], listName);
			}
		}

		return res;
	}

	/**
	* Checks if provided string array is null or empty.
	*
	* @param str String array to check.
	* @return True if null or empty.
	*/
	private boolean isEmpty(String str[])
	{
		return(str == null || str.length == 0);
	}
	
	/**
	 * Adds attachments to specified mail.
	 * @param multiPart Multipart mail to which attachments needs to be attached.
	 * @param context Context to be used which is expected have attachments.
	 */
	private void addAttachments(Multipart multiPart, Object context) throws Exception
	{
		if(context == null)
		{
			return;
		}
		
		MailTemplateConfiguration mailTemplateConfiguration = mailTemplateConfigService.getMailTemplateConfiguration(context.getClass());
		
		if(mailTemplateConfiguration == null)
		{
			logger.warn("As specified context is not defined as mail template configuration, no attachments will be added. Context type: " + context.getClass().getName());
			return;
		}
		
		Set<MailTemplateConfiguration.Attachment> attachmentConfigs = mailTemplateConfiguration.getAttachments();
		
		if(attachmentConfigs == null || attachmentConfigs.isEmpty())
		{
			return;
		}
		
		FileDataSource fileSource = null;
		MimeBodyPart fileBodyPart = null;
		
		Object value = null;
		File fieldFile = null;
		String attachmentName = null;
		
		for(MailTemplateConfiguration.Attachment attachment : attachmentConfigs)
		{
			value = ReflectionUtils.getNestedFieldValue(context, attachment.getField());
			
			if(value == null)
			{
				continue;
			}
			
			logger.debug("Adding attachment from field - " + attachment.getField());
			
			if(value instanceof File)
			{
				fieldFile = (File) value;
			}
			else if(value instanceof String)
			{
				fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				FileUtils.write(fieldFile, (String) value);
			}
			else if(value instanceof byte[])
			{
				fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				FileUtils.writeByteArrayToFile(fieldFile, (byte[]) value);
			}
			else if(value instanceof Image)
			{
				attachmentName = attachment.getName();
				
				fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				String imgType = attachmentName.substring(attachmentName.lastIndexOf('.') + 1, attachmentName.length());
				
				if(!(value instanceof RenderableImage))
				{
					Image img = (Image) value;
					BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
					bimg.getGraphics().drawImage(img, 0, 0, null);
					
					value = bimg;
				}
				
				ImageIO.write((RenderedImage) value, imgType.toLowerCase(), fieldFile);
			}
			else
			{
				throw new InvalidStateException("Field {}.{} is marked as attachment with unsupported type", context.getClass().getName(), attachment.getField());
			}
			
			fileBodyPart = new MimeBodyPart();
			fileSource = new FileDataSource(fieldFile);
			
			fileBodyPart.setDataHandler(new DataHandler(fileSource));
			fileBodyPart.setFileName(attachment.getName());
			fileBodyPart.setHeader("Content-ID", "<" + attachment.getContentId() + ">");
			
			multiPart.addBodyPart(fileBodyPart);
		}
	}

	/**
	 * Builds the mail message from specified email data.
	 * @param settings Setti/ngs to be used.
	 * @param emailData Email data from which messaged needs to be built.
	 * @param context Context to be used for freemarker expressions parsing.
	 * @return Converted message.
	 */
	private Message buildMessage(EmailServerSettings settings, EmailData emailData, Object context) throws AddressException, MessagingException
	{
		if(isEmpty(emailData.getToList()) && isEmpty(emailData.getCcList()) && isEmpty(emailData.getBccList()))
		{
			throw new InvalidArgumentException("No recipient email id specified in any of the email list");
		}
		
		//start new session
		Session mailSession = newSession(settings);

		// build the mail message
		Message message = new MimeMessage(mailSession);

		try
		{
			message.setFrom(new InternetAddress(settings.getUserName()));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while parsing from mail id - {}", settings.getUserName());
		}

		//set recipients mail lists
		if(!isEmpty(emailData.getToList()))
		{
			message.setRecipients(Message.RecipientType.TO, convertToInternetAddress("To", emailData.getToList()));
		}

		if(!isEmpty(emailData.getCcList()))
		{
			message.setRecipients(Message.RecipientType.CC, convertToInternetAddress("CC", emailData.getCcList()));
		}

		if(!isEmpty(emailData.getBccList()))
		{
			message.setRecipients(Message.RecipientType.BCC, convertToInternetAddress("BCC", emailData.getBccList()));
		}

		//set the subject
		String subject = freeMarkerService.processTemplate(emailData.getTemplateName() + ".subject", emailData.getSubjectTemplate(), context);
		message.setSubject(subject);
		message.setSentDate(new Date());
		
		//create multi part message
		Multipart multiPart = new MimeMultipart();
		
		//add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		String content = freeMarkerService.processTemplate(emailData.getTemplateName() + ".content", emailData.getContentTemplate(), context);
		messageBodyPart.setContent(content, "text/html");
		messageBodyPart.addHeader(HEADER_NAME_WEBUTILS, "true");
		
		multiPart.addBodyPart(messageBodyPart);
		
		//add files if any
		try
		{
			addAttachments(multiPart, context);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while setting attachments", ex);
		}
		
		//set the multi part message as content
		message.setContent(multiPart);
		
		return message;
	}

	/**
	 * Sends the specified email message.
	 * @param settings Email server settings to be used.
	 * @param email Email data to be used.
	 * @param context Context to be used for processing.
	 */
	public void sendEmail(EmailServerSettings settings, EmailData email, Object context)
	{
		try
		{
			// Build mail message object
			Message message = buildMessage(settings, email, context);

			// send the message
			Transport.send(message);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while sending email - {}", email);
		}
	}
	
	/**
	 * Extracts mail content into specified mail message.
	 * @param mailMessage Mail message to which content needs to be fetched.
	 * @param content Content to be parsed into mail message.
	 * @param contentType Content type.
	 */
	private void extractMailContent(MailMessage mailMessage, Object content, String contentType) throws MessagingException, IOException
	{
		if("multipart".equals(contentType))
		{
			mailMessage.addMessagePart(new MailMessage.MessagePart(content.toString(), null));
			return;
		}
		
		Multipart multipart = (Multipart) content;
		int count = multipart.getCount();
		BodyPart part = null;
		File attachmentFile = null;
		
		for(int i = 0; i < count; i++)
		{
			part = multipart.getBodyPart(i);
			
			if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
			{
				attachmentFile = File.createTempFile(part.getFileName(), ".attachment");
				((MimeBodyPart) part).saveFile(attachmentFile);
				
				mailMessage.addAttachment(new MailMessage.Attachment(attachmentFile, part.getFileName()));
			}
			else
			{
				//part.getAllHeaders().nextElement()
				mailMessage.addMessagePart(new MailMessage.MessagePart(part.getContent().toString(), null));
			}
		}
	}
	
	/**
	 * Reads mails from specified folder and for each mail, mail-processor will be invoked.
	 * @param store Mails store to check.
	 * @param folderName Folder name to check.
	 * @param mailProcessor Processor for processing mails.
	 */
	private void readMailsFromFolder(Store store, String folderName, IMailProcessor mailProcessor) throws MessagingException, IOException
	{
		Folder mailFolder = store.getFolder(folderName);
		mailFolder.open(Folder.READ_WRITE);

		Message[] messages = mailFolder.getMessages();

		for(int i = 0; i < messages.length; i++)
		{
			Message message = messages[i];
			String subject = message.getSubject();

			String nameMailId = message.getFrom()[0].toString(); 
			String frmMailId = nameMailId.substring(nameMailId.indexOf("<") + 1 , nameMailId.indexOf(">")).trim();

			MailMessage mailMessage = new MailMessage(frmMailId, subject);
			extractMailContent(mailMessage, message.getContent(), message.getContentType());
			
			if(mailProcessor.processAndDelete(mailMessage))
			{
				message.setFlag(Flags.Flag.DELETED, true);
			}
		}

		mailFolder.close(false);
	}

	/**
	 * Reads the mails from the email server specified by settings.
	 * @param settings Mail server settings from which mails has to be read.
	 * @param mailProcessor Processor to process read mails.
	 */
	public void readMails(EmailServerSettings settings, IMailProcessor mailProcessor)
	{
		try
		{
			Session session = newSession(settings);

			Store store = session.getStore(settings.getReadProtocol().getName());
			store.connect(settings.getReadHost(), settings.getUserName(), settings.getPassword());

			for(String folderName : settings.getFolderNames())
			{
				readMailsFromFolder(store, folderName, mailProcessor);
			}
			
			store.close();
		} catch(Exception e)
		{
			throw new IllegalStateException("Exception occured while reading the mail ", e);
		}
	}
}
