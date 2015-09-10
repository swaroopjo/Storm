package com.lio.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailService {
	

	private Properties properties;
	private String host;
	private String port;
	private String toAddress;
	private String fromAddress;
	
	
	public EmailService(){

		this.host="smtprelay.mayo.edu";
		this.port = 25+"";
		this.toAddress = "Pamalla.Jyothiswaroop@mayo.edu";
		this.fromAddress = "Pamalla.Jyothiswaroop@mayo.edu";
		
		properties = System.getProperties();
		properties.setProperty("mail.smtp.host", this.host);
		properties.setProperty("mail.smtp.port", this.port);
	}
	
	@Override
	public String toString() {
		String rv = null;
		if(
				this.host == null ||
				this.port == null ||
				this.toAddress == null ||
				this.fromAddress == null) 
		{
			rv = "Undefined";
		}
		else 
		{
			rv = "host=" + this.host + ", port=" + this.port + ", toAddress=" + this.toAddress + ", fromAddress=" + this.fromAddress;
		}
		return rv;
	}
	
	public void sendEmail(String subject, String text) throws MessagingException {
		   // Get the default Session object.
	      Session session = Session.getDefaultInstance(this.properties);
       // Create a default MimeMessage object.
       MimeMessage message = new MimeMessage(session);

       // Set From: header field of the header.
       message.setFrom(new InternetAddress(this.fromAddress));

       // Set To: header field of the header.
       String[] toAddresses = this.toAddress.split(",");
       for(String s : toAddresses) {
           message.addRecipient(Message.RecipientType.TO,
                   new InternetAddress(s));        	 
       }

       // Set Subject: header field
       message.setSubject(subject);

       // Now set the actual message
       message.setText(text);

       // Send message
       Transport.send(message);
	}

}
