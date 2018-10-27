package com.authrus.sso.mail;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zuooh.common.mail.MailClient;
import com.zuooh.common.mail.MailSender;

@Configuration
@ComponentScan(basePackageClasses = MailConfiguration.class)
public class MailConfiguration {

   private final File credentials;
   private final String host;
   private final int port;

   public MailConfiguration(
         @Value("${mail.credentials}") File credentials,
         @Value("${mail.host}") String host,
         @Value("${mail.port:465}") int port) 
   {
      this.credentials = credentials;
      this.host = host;
      this.port = port;
   }
   
   @Bean
   public MailClient mailClient() {
      Properties properties = new Properties();
      Authenticator authenticator = new FileAuthenticator(credentials);
      
      properties.put("mail.transport.protocol", "smtp");
      properties.put("mail.smtp.port", port); 
      properties.put("mail.smtp.host", host);
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.auth", "true");
      
      return new MailSender(properties, authenticator);
   }
}
