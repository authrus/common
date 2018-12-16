package com.authrus.common.mail;

import static com.authrus.common.mail.MailType.TEXT;
import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description="Sends a mail over SMTP")
public class MailSender implements MailClient {

   private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);

   private final Properties properties;
   private final Session session;
   
   public MailSender(Properties properties) {
      this(properties, null, null);
   }

   public MailSender(Properties properties, String user, String password) {
      this(properties, new MailAuthenticator(user, password));
   }
   
   public MailSender(Properties properties, Authenticator authenticator) {
      this.session = Session.getDefaultInstance(properties, authenticator);
      this.properties = properties;
   }

   @ManagedOperation(description="Send a mail")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="from", description="From address"), 
      @ManagedOperationParameter(name="to", description="To address"),
      @ManagedOperationParameter(name="subject", description="Mail subject"), 
      @ManagedOperationParameter(name="body", description="Mail body") 
   })
   public void sendMessage(String from, String to, String subject, String body) {
      MailAddress address = new MailAddress(from);
      MailMessage message = new MailMessage(TEXT, address, to, subject, body);
      
      message.setBody(body);      
      sendMessage(message);
   }

   @Override
   public void sendMessage(MailMessage message) {
      MimeMessage envelope = new MimeMessage(session);
      MimeBodyPart body = new MimeBodyPart();
      MimeMultipart content = new MimeMultipart();

      try {
         createHeaders(message, envelope);
         createRecipients(message, envelope);
         createAttachments(message, content);
         createBody(message, body);

         content.addBodyPart(body);         
         envelope.setContent(content);         
         Transport.send(envelope);
      } catch (MessagingException e) {
         LOG.info("Could not sent mail message", e);
      }
   } 
   
   private void createBody(MailMessage message, MimeBodyPart body) {
      try {
         MailType type = message.getType();
         String category = type.getSubType();
         String charset = type.getCharset();   
         String text = message.getBody();
         
         body.setText(text, charset, category);
      } catch(Exception e) {
         throw new IllegalStateException("Could not build message body", e);
      }
   }
   
   private void createAttachments(MailMessage message, MimeMultipart content) {
      Set<String> names = new HashSet<String>();
      
      try {
         Set<MailAttachment> attachments = message.getAttachments();
         
         for (MailAttachment attachment : attachments) {
            MimeBodyPart part = attachment.getPart();
            String name = attachment.getName();
            
            if(names.add(name)) {
               long size = attachment.getSize();
            
               if(size > 0) {
                  content.addBodyPart(part);
               }
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not build attachments", e);
      }
   }

   private void createRecipients(MailMessage message, MimeMessage envelope) {
      Set<InternetAddress> addresses = new HashSet<InternetAddress>();
      
      try {
         Set<MailRecipient> recipients = message.getRecipients();
         
         for (MailRecipient recipient : recipients) {
            MailAddress mail = recipient.getAddress();            
            MailRecipientType type = recipient.getType();            
            InternetAddress address = mail.getAddress();
            String value = mail.getMail();
            
            if(!value.isEmpty() && addresses.add(address)) {
               if(type.isTo()) {
                  envelope.addRecipient(TO, address);
               }
               if(type.isCarbonCopy()) {
                  envelope.addRecipient(CC, address);
               }
               if(type.isBlindCarbonCopy()) {
                  envelope.addRecipient(BCC, address);
               }
            }
         }       
      } catch(Exception e) {
         throw new IllegalStateException("Could not build recipient list", e);
      }      
   }
   
   private void createHeaders(MailMessage message, MimeMessage envelope) {
      try {
         String subject = message.getSubject();
         MailAddress from = message.getFrom();
         InternetAddress address = from.getAddress();
         
         if(subject != null) {
            envelope.setSubject(subject);
         }
         envelope.setFrom(address);
      } catch(Exception e) {
         throw new IllegalStateException("Could not build message header", e);
      }  
   }
}
