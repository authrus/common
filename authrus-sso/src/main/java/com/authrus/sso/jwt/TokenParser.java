package com.authrus.sso.jwt;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenParser {
   
   private final TypeReference<Map<String, String>> type;
   private final TokenGenerator generator;
   private final ObjectMapper mapper;

   public TokenParser(TokenGenerator generator) {
      this.type = new TypeReference<Map<String, String>>(){};
      this.mapper = new ObjectMapper();
      this.generator = generator;
   }
   
   public Token parseToken(String cookie) {
     int first = cookie.indexOf(".");
     int last = cookie.lastIndexOf(".");
     
     if(first < last && first != -1) {
        try {
           String value = cookie.substring(first + 1, last);
           byte[] data = Base64.getDecoder().decode(value);
        
           if(data.length > 0) {
              Map<String, String> claims = mapper.readValue(data, type);
              String time = claims.get("expiry");
              Instant instant = Instant.parse(time);
              long expiry = instant.toEpochMilli();
              Token token = generator.generateToken(claims, expiry);
              String text = token.getToken();
              
              if(cookie.equals(text)) {
                 return token;
              }
           }
        } catch(Exception e) {
           return null;
        }
     }
     return null;
   }
}
