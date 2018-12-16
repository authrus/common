package com.authrus.common.mail;

import junit.framework.TestCase;

public class MailAddressTest extends TestCase {

   public void testAddress() throws Exception {
      MailAddress address = new MailAddress("Authrus <admin@authrus.com>");
      
      assertEquals(address.getAddress().getPersonal(), "Authrus");
      assertEquals(address.getAddress().getAddress(), "admin@authrus.com");
   }
}
