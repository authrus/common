package com.authrus.sso.access;

import com.authrus.store.Entity;
import com.authrus.store.PrimaryKey;

import java.io.Serializable;

import javax.ws.rs.core.NewCookie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Entity
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessGrant implements Serializable {

   @PrimaryKey
   private final String email;
   private final String redirect;
   private final String cookie;
   private final long expiry;
   
   public NewCookie getCookie() {
      return new NewCookie("SSOID", cookie, "/", null, null, (int)(expiry / 1000), false);
   }
}
