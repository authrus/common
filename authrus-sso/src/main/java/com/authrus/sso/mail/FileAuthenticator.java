package com.authrus.sso.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import lombok.SneakyThrows;

public class FileAuthenticator extends Authenticator {
   
   private final File file;
   
   public FileAuthenticator(File file) {
      this.file = file;     
   }
   
   @SneakyThrows
   protected PasswordAuthentication getPasswordAuthentication() {
      Properties properties = new Properties();
      InputStream stream = new FileInputStream(file);
      
      try {
         properties.load(stream);
      } finally {
         stream.close();
      }
      String user = properties.getProperty("user");
      String password = properties.getProperty("password");      
      
      return new PasswordAuthentication(user, password);
   } 

}
