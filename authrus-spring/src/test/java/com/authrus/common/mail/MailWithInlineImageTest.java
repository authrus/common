package com.authrus.common.mail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import junit.framework.TestCase;

public class MailWithInlineImageTest extends TestCase {

   public void testInlineImageWithAttachment() throws IOException {
      Properties properties = new Properties();

      properties.setProperty("mail.smtp.host", "smtp.sendgrid.net");
      properties.setProperty("mail.smtp.port", "587");
      properties.setProperty("mail.smtp.auth", "true");

      File file = new File("C:\\Work\\development\\bitbucket\\proxy\\zuooh-standard-sso\\template\\login\\img\\logo_very_small.png");
      MailSender sender = new MailSender(properties, "niallg", "bevendenN16BL");  
      MailAttachment image = new ImageAttachment(file, "image");
      MailAttachment attachment = new ByteArrayAttachment("This is some message".getBytes(), "testFile.txt", "text/plain");
      MailAddress from = new MailAddress("niall.gallagher@zuooh.com");
      MailRecipient recipient = new MailRecipient(MailRecipientType.TO, "gallagher_niall@yahoo.com");
      MailMessage message = new MailMessage(MailType.HTML, from, recipient, "Test mail", "<img src=\"cid:image\"><br><h2>Hello</h2> This is a test mail");

      message.addAttachment(image);
      message.addAttachment(attachment);
      sender.sendMessage(message);
   }
   

   public void testInlineImage() throws IOException {
      Properties properties = new Properties();

      properties.setProperty("mail.smtp.host", "smtp.sendgrid.net");
      properties.setProperty("mail.smtp.port", "587");
      properties.setProperty("mail.smtp.auth", "true");

      File file = new File("C:\\Work\\development\\bitbucket\\proxy\\zuooh-standard-sso\\template\\login\\img\\logo_very_small.png");
      MailSender sender = new MailSender(properties, "niallg", "bevendenN16BL");  
      MailAttachment attachment = new ImageAttachment(file, "image");
      MailAddress from = new MailAddress("niall.gallagher@zuooh.com");
      MailRecipient recipient = new MailRecipient(MailRecipientType.TO, "gallagher_niall@yahoo.com");
      MailMessage message = new MailMessage(MailType.HTML, from, recipient, "Test mail", "<img src=\"cid:image\"><br><h2>Hello</h2> This is a test mail");

      message.addAttachment(attachment);
      sender.sendMessage(message);
   }
}
