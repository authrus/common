package com.authrus.sso.access;

import com.authrus.store.Entity;
import com.authrus.store.PrimaryKey;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Maps;

@Data
@Entity
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequest implements Serializable {

   @PrimaryKey
   private final String email;
   private final String redirect;
   private final String token;
   private final long expiry;
   private final long lease;
   private final int code;
   
   public Map<String, String> getClaims() {
      Map<String, String> claims = Maps.newHashMap();
      Instant instant = Instant.ofEpochMilli(expiry);
      String time = instant.toString();
      
      claims.put("email", email);
      claims.put("token", token);
      claims.put("expiry", time);
      
      return claims;
   }
}
