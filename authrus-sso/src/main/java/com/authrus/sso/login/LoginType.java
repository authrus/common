package com.authrus.sso.login;

import com.authrus.sso.access.AccessRequest;

public enum LoginType {
   OTP {
      @Override
      public Login generateLogin(AccessRequest request, String base) {
         int code = request.getCode();
         String address = String.format("%s/login/access-code", base);
         String redirect = String.format("%s/mail-sent", base);
         
         address = address.replace("//login/", "/login/");
         redirect = redirect.replace("//mail-sent", "/mail-sent");
         
         return new Login(this, redirect, "Please enter " + code + " to " + address, true);
      }
   },
   TOKEN {
      @Override
      public Login generateLogin(AccessRequest request, String base) {
         String token = request.getToken();
         String address = String.format("%s/login/access/grant/%s", base, token);
         String redirect = String.format("%s/mail-sent", base);
         
         address = address.replace("//login/", "/login/");
         redirect = redirect.replace("//mail-sent", "/mail-sent");
         
         return new Login(this, redirect, "Please click " + address, true);
      }
   },
   PASSWORD {
      @Override
      public Login generateLogin(AccessRequest request, String base) {
         String token = request.getToken();
         String address = String.format("%s/login/access/grant/%s", base, token);

         address = address.replace("//login/", "/login/");
         
         return new Login(this, address, address, false);
      }
   };
  
   public abstract Login generateLogin(AccessRequest request, String base);

   public static LoginType resolveType(String token) {
      if(token != null) {
         LoginType[] types = LoginType.values();
         
         for(LoginType type : types) {
            String name = type.name();
            
            if(name.equalsIgnoreCase(token)) {
               return type;
            }
         }
      }
      return TOKEN;
   }
}
