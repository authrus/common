package com.authrus.sso.login;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login {

   private final LoginType type;
   private final String redirect;
   private final String message;
   private final boolean mail;

   public URI getRedirect() {
      return URI.create(redirect);
   }
}
