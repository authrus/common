package com.authrus.gateway.deploy.build;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class HealthCheck {
   
   private final Map<String, String> headers;
   private final String path;
   private final long frequency;
   
   @JsonCreator
   public HealthCheck(
         @JsonProperty("ping-headers") Map<String, String> headers,
         @JsonProperty("ping-path") String path,
         @JsonProperty("ping-frequency") long frequency)
   {
      this.headers = headers;
      this.frequency = frequency;
      this.path = path;
   }
   
   
   public Map<String, String> getHeaders(){
      return headers == null ? Collections.EMPTY_MAP : headers;
   }
   
   public long getFrequency() {
      if(frequency <= 0) {
         return 10000;
      }
      return frequency;
   }
   
   public String getPath(){
      return path;
   }
   
   public void validate(List<String> servers) {
      boolean hasHeaders = headers != null && headers.size() > 0;
      
      if(path == null && hasHeaders) {
         throw new IllegalStateException("Ping path not specified for " + servers);
      }
//      if(frequency <= 0) {
//         frequency = 10000;
//      }
   }
}