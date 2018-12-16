package com.authrus.common.mail;

import junit.framework.TestCase;

public class MailRecipientTypeTest extends TestCase {

   public void testRecipientLists() throws Exception {
      MailRecipient[] recipients = (MailRecipient[])MailRecipientType.TO.getRecipients("niall.gallagher@yieldbroker.com").toArray(new MailRecipient[0]);

      assertEquals(recipients.length, 1);
      assertEquals(recipients[0].getType(), MailRecipientType.TO);
      assertEquals(recipients[0].getAddress().getMail(), "niall.gallagher@yieldbroker.com");
   }
   
   public void testRecipientList() throws Exception {
      MailRecipient[] recipients = (MailRecipient[])MailRecipientType.TO.getRecipients("niall.gallagher@yieldbroker.com,alison.howes@yieldbroker.com").toArray(new MailRecipient[0]);

      assertEquals(recipients.length, 2);
      assertEquals(recipients[0].getType(), MailRecipientType.TO);
      assertEquals(recipients[0].getAddress().getMail(), "alison.howes@yieldbroker.com");      
      assertEquals(recipients[1].getType(), MailRecipientType.TO);
      assertEquals(recipients[1].getAddress().getMail(), "niall.gallagher@yieldbroker.com");
   }

}
