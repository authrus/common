package com.authrus.common.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
   
   private final String user;
   private final String password;
   
   public MailAuthenticator() {
      this(null, null);
   }
   
   public MailAuthenticator(String user, String password) {
      this.password = password;
      this.user = user;      
   }
   
   protected PasswordAuthentication getPasswordAuthentication() {
      if(user != null) {
         return new PasswordAuthentication(user, password);
      }
      return null;
   } 

}
