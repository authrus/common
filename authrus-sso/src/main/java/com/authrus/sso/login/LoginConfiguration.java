package com.authrus.sso.login;

import com.authrus.sso.access.AccessService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = LoginConfiguration.class)
public class LoginConfiguration {

   private final String directory;
   private final String from;
   private final String base;
   
   public LoginConfiguration(
         @Value("${login.url.directory}") String directory,
         @Value("${login.url.base}") String base,
         @Value("${login.email}") String from)
   {
      this.directory = directory;
      this.from = from;
      this.base = base;
   }
   
   @Bean
   public LoginService loginService(LoginClient client, AccessService service) {
      return new LoginService(service, client, directory, base, from);
   }
}
