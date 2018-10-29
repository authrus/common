package com.authrus.gateway.deploy.build;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.resource.redirect.Redirect;
import com.zuooh.http.proxy.resource.redirect.RequestHeaderRedirect;
import com.zuooh.http.proxy.resource.redirect.RequestHeaderVerifier;

class CookieRule {
   
   private final HeaderValidation validation;
   private final String pattern;
   private final String name;
   private final boolean present;
   
   @JsonCreator
   public CookieRule(
         @JsonProperty(value = "validation", required = true) HeaderValidation validation,
         @JsonProperty(value = "cookie-pattern", required = true) String pattern,
         @JsonProperty(value = "cookie-name", required = true) String name,
         @JsonProperty(value = "cookie-present", required = true) String present)
   {
      this.present = Boolean.parseBoolean(present);
      this.validation = validation;
      this.pattern = pattern;
      this.name = name;
   }
   
   @SneakyThrows
   public Redirect createRedirect(Redirect redirect) {
      Map<String, String> headers = validation.getHeaders();
      Set<String> addresses = validation.getAddresses();
      long expiry = validation.getExpiry();
      
      if(pattern == null) {
         throw new IllegalStateException("Cookie rule requires a cookie value pattern");
      }
      if(pattern == null) {
         throw new IllegalStateException("Cookie rule requires a cookie name");
      }
      if(validation == null) {
         throw new IllegalStateException("Cookie rule requires validation urls");
      }      
      if(addresses == null || addresses.isEmpty()) {
         throw new IllegalStateException("Validation addresses must not be empty");
      }
      RequestHeaderVerifier verifier = new RequestHeaderVerifier(addresses, headers == null ? Collections.EMPTY_MAP : headers);
      return new RequestHeaderRedirect(verifier, redirect, name, pattern, true, present, expiry); 
   }
}