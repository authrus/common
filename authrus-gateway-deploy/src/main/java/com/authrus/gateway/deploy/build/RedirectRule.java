package com.authrus.gateway.deploy.build;

import org.simpleframework.http.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.resource.redirect.Redirect;
import com.zuooh.http.proxy.resource.redirect.RegularExpressionRedirect;

class RedirectRule {
   
   private final HeaderRule headerRule;
   private final CookieRule cookieRule;
   private final String match;
   private final String ignore;
   private final String template;

   @JsonCreator
   public RedirectRule(
         @JsonProperty("header-rule") HeaderRule headerRule,
         @JsonProperty("cookie-rule") CookieRule cookieRule,
         @JsonProperty("match-pattern") String match,
         @JsonProperty("ignore-pattern") String ignore,
         @JsonProperty("template") String template)
   {
      this.headerRule = headerRule;
      this.cookieRule = cookieRule;
      this.template = template;
      this.match = match;
      this.ignore = ignore;
   }
   
   public RedirectEntry createRedirect() {
      String source = match.toLowerCase();
      String destination = template.toLowerCase();
      
      if(cookieRule != null && headerRule != null) {
         throw new IllegalStateException("Header rule cannot be used with cookie rule");
      }
      if(!source.startsWith("http://") && !source.startsWith("https://")) {
         throw new IllegalStateException("Redirect must clearly specify the scheme");
      }
      if(!destination.startsWith("http://") && !destination.startsWith("https://")) {
         throw new IllegalStateException("Redirect must clearly specify the scheme");
      }
      if(ignore != null) {
         String value = ignore.toLowerCase();
         
         if(!value.startsWith("http://") && !value.startsWith("https://")) {
            throw new IllegalStateException("Redirect must clearly specify the scheme");
         }
      }
      try {
         String sourcePath = extractPath(match);
         String destinationPath = extractPath(template);
         String ignorePath = ignore == null ? "/^" : extractPath(ignore);
         Scheme destinationScheme = destination.startsWith("https://") ? Scheme.HTTPS : Scheme.HTTP;  
         Scheme sourceScheme = source.startsWith("https://") ? Scheme.HTTPS : Scheme.HTTP;  
         Redirect redirect = new RegularExpressionRedirect(destinationScheme, sourcePath, destinationPath, ignorePath); 
         
         if(headerRule != null) {
            redirect = headerRule.createRedirect(redirect);
         } else if(cookieRule != null){
            redirect = cookieRule.createRedirect(redirect);
         }
         return new RedirectEntry(redirect, sourceScheme, sourcePath);
      }catch(Exception e) {
         throw new IllegalStateException("Could not parse redirect expression", e);
      }
   }
   
   private String extractPath(String address) {
      int schemeEnd = address.indexOf("://");
     
      if(schemeEnd != -1) {
         address = address.substring(schemeEnd + 3);
      }
      int pathBegin = address.indexOf("/");
      
      if(pathBegin != -1){
         return address.substring(pathBegin);
      }
      return "/";
   }
}