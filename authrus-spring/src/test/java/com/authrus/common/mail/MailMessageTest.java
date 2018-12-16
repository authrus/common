package com.authrus.common.mail;

import static com.authrus.common.mail.MailRecipientType.TO;

import junit.framework.TestCase;

public class MailMessageTest extends TestCase {
   
   public void testMailMessageWithSingleRecipient() throws Exception {
      MailAddress from = new MailAddress("niall.gallagher@yieldbroker.com");
      MailMessage message = new MailMessage(MailType.HTML, from, "niall.gallagher@yieldbroker.com", "Test Message", "<h1>This</h1> is the contents of the HTML message");
      
      assertEquals(message.getType(), MailType.HTML);
      assertEquals(message.getSubject(), "Test Message");
      assertEquals(message.getBody(), "<h1>This</h1> is the contents of the HTML message");
      assertEquals(message.getRecipients().size(), 1);
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[0].getAddress().getMail(), "niall.gallagher@yieldbroker.com");      
      assertEquals(message.getFrom().getMail(), "niall.gallagher@yieldbroker.com");
      assertEquals(message.getAttachments().size(), 0);
   }
   
   public void testMailMessageWithMultipleRecipients() throws Exception {
      MailAddress from = new MailAddress("niall.gallagher@yieldbroker.com");
      MailMessage message = new MailMessage(MailType.HTML, from, "niall.gallagher@yieldbroker.com,alison.howes@yieldbroker.com,david.gardner@yieldbroker.com", "Test Message", "<h1>This</h1> is the contents of the HTML message");
      
      assertEquals(message.getType(), MailType.HTML);
      assertEquals(message.getSubject(), "Test Message");
      assertEquals(message.getBody(), "<h1>This</h1> is the contents of the HTML message");
      assertEquals(message.getRecipients().size(), 3);
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[0].getAddress().getMail(), "alison.howes@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[1].getAddress().getMail(), "david.gardner@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[2].getAddress().getMail(), "niall.gallagher@yieldbroker.com");           
      assertEquals(message.getFrom().getMail(), "niall.gallagher@yieldbroker.com");
      assertEquals(message.getAttachments().size(), 0);
   }
   
   public void testMailMessageDoesNotAllowDuplicateRecipients() throws Exception {
      MailAddress from = new MailAddress("niall.gallagher@yieldbroker.com");
      MailMessage message = new MailMessage(MailType.HTML, from, "niall.gallagher@yieldbroker.com,niall.gallagher@yieldbroker.com,alison.howes@yieldbroker.com,david.gardner@yieldbroker.com", "Test Message", "<h1>This</h1> is the contents of the HTML message");
      
      assertEquals(message.getType(), MailType.HTML);
      assertEquals(message.getSubject(), "Test Message");
      assertEquals(message.getBody(), "<h1>This</h1> is the contents of the HTML message");
      assertEquals(message.getRecipients().size(), 3);
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[0].getAddress().getMail(), "alison.howes@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[1].getAddress().getMail(), "david.gardner@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[2].getAddress().getMail(), "niall.gallagher@yieldbroker.com");           
      assertEquals(message.getFrom().getMail(), "niall.gallagher@yieldbroker.com");
      assertEquals(message.getAttachments().size(), 0);
      
      message.addRecipient(TO, "niall.gallagher@yieldbroker.com");
      
      assertEquals(message.getRecipients().size(), 3);
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[0].getAddress().getMail(), "alison.howes@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[1].getAddress().getMail(), "david.gardner@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[2].getAddress().getMail(), "niall.gallagher@yieldbroker.com");
      
      message.addRecipient(TO, "alex.samad@yieldbroker.com");
      message.addRecipient(TO, "daniel.poon@yieldbroker.com");
      
      assertEquals(message.getRecipients().size(), 5);
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[0].getAddress().getMail(), "alex.samad@yieldbroker.com");
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[1].getAddress().getMail(), "alison.howes@yieldbroker.com");
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[2].getAddress().getMail(), "daniel.poon@yieldbroker.com");         
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[3].getAddress().getMail(), "david.gardner@yieldbroker.com");     
      assertEquals(message.getRecipients().toArray(new MailRecipient[0])[4].getAddress().getMail(), "niall.gallagher@yieldbroker.com");  
   }

}
