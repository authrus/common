package com.authrus.sso.otp;

import com.authrus.store.Entity;
import com.authrus.store.PrimaryKey;

import java.io.Serializable;

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
public class PassCode implements Serializable {

   @PrimaryKey
   private final int code;
   private final long expiry;
   private final String email;
}
