package com.authrus.sso.login;

import com.authrus.domain.user.User;
import com.authrus.sso.access.AccessRequest;
import com.authrus.sso.access.AccessService;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoginService {
   
   private final AccessService service;
   private final LoginClient client;
   private final String directory;
   private final String base;
   private final String from;

   public Login login(String email, String password, String redirect, String mode) {
      AccessRequest request = service.createRequest(email, redirect);
      LoginType type = LoginType.resolveType(mode);
      Login login = type.generateLogin(request, base);
      
      if(login.isMail()) {
         String message = login.getMessage();
         client.login(email, from, directory, message);
      }
      return login;
   }
   
   public Login register(User user, String redirect, String mode) {
      User registered = client.register(user, directory);
      String email = registered.getEmail();
      String password = registered.getPassword();
      
      return login(email, password, redirect, mode);
   }
}
