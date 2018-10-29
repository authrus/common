package com.authrus.gateway.deploy.build;

import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
class HeaderValidation {

   private final Map<String, String> headers;
   private final Set<String> addresses;
   private final long expiry;
   
   @JsonCreator
   public HeaderValidation(
         @JsonProperty(value = "validation-headers", required = false) Map<String, String> headers,
         @JsonProperty(value = "validation-services", required = true) Set<String> addresses,
         @JsonProperty(value = "expiry", required = false) long expiry)
   {
      this.headers = headers;
      this.addresses = addresses;
      this.expiry = expiry;
   }
}