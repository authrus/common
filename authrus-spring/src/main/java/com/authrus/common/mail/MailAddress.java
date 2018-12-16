package com.authrus.common.mail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailAddress {

   private String mail;
   
   public MailAddress(String mail) {
      this.mail = mail;
   }
   
   public MailAddress(MailAddress address) {
      this(address.mail);
   }
   
   public InternetAddress getAddress() throws AddressException {
      return new InternetAddress(mail); 
   }
   
   public String getMail() {
      return mail;
   }
   
   public void setMail(String mail) {
      this.mail = mail;
   }
   
   @Override
   public String toString() {
      return mail;
   }
}
