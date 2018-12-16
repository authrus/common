package com.authrus.common.mail;

import java.util.Properties;

import junit.framework.TestCase;

public class MailSenderTest extends TestCase {

   public void testSender() throws Exception {
      Properties properties = new Properties();

      properties.setProperty("mail.smtp.host", "smtp.yieldbroker.com");

      MailSender sender = new MailSender(properties);
      MailAttachment attachment = new ByteArrayAttachment("This is some message".getBytes(), "testFile.txt", "text/plain");
      MailAddress from = new MailAddress("niall.gallagher@yieldbroker.com");
      MailMessage message = new MailMessage(MailType.TEXT, from, "niall.gallagher@yieldbroker.com", "Test Message", "This is the contents of the message");

      message.addAttachment(attachment);
      sender.sendMessage(message);
   }

   public void testHtmlSender() throws Exception {
      Properties properties = new Properties();

      properties.setProperty("mail.smtp.host", "smtp.yieldbroker.com");

      MailSender sender = new MailSender(properties);      
      MailAttachment attachment = new ByteArrayAttachment("This is some message".getBytes(), "testFile.txt", "text/plain");
      MailAddress from = new MailAddress("niall.gallagher@yieldbroker.com");
      MailMessage message = new MailMessage(MailType.HTML, from, "niall.gallagher@yieldbroker.com", "Test Message", "<h1>This</h1> is the contents of the HTML message");

      message.addAttachment(attachment);
      sender.sendMessage(message);
   }
}
