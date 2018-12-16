package com.authrus.transport.tunnel;

public enum TunnelState {
   ESTABLISHED,
   UNAUTHORIZED,
   FORBIDDEN,
   REJECTED,
   UNKNOWN,
   ERROR;
   
   public static TunnelState resolveState(String code) {
      if(code.startsWith("200")) {
         return ESTABLISHED;
      }
      if(code.startsWith("500")) {
         return ERROR;
      }
      if(code.startsWith("403")) {
         return FORBIDDEN;
      }
      if(code.startsWith("401")) {
         return UNAUTHORIZED;
      }         
      return UNKNOWN;
   }
}
