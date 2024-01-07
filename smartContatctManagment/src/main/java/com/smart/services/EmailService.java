package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEMail(String subject,String message, String to) {
		boolean b=false;
		// Send Email service
		//From 
		String from="pavanparaskar3@gmail.com";
		
		String host="smtp.gmail.com";
		Properties properties=System.getProperties();
		
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		
		//Step 1 get session object
	Session session=Session.getInstance(properties, new Authenticator() {
		
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			
			return new PasswordAuthentication("pavanparaskar3@gmail.com", "Vpceg28fn");
		}
		
	}
	
			
			);
	session.setDebug(true);
	
	
	//setep 2
	MimeMessage m=new MimeMessage(session);
	try {
		m.setFrom(from);
		m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		m.setSubject(subject);
		m.setText(message);
		Transport.send(m);
		b=true;
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	
	return b;
	}

}
