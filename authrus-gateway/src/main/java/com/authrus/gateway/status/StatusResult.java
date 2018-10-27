package com.authrus.gateway.status;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zuooh.http.proxy.core.State;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class StatusResult {

   private final List<String> patterns;
   private final ConnectionPool pool;
   private final State state;
   private final String local;
   private final String remote;
   private final String address;
   
   @Data
   @Builder
   @AllArgsConstructor
   @JsonInclude(Include.NON_NULL)
   public static class ConnectionPool {
      
      private final State state;      
      private final String age;
      private final boolean alive;
      private final int failures;
      private final int active;
      private final int idle;
   }
  
}
