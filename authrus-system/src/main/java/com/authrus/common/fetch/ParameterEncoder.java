package com.authrus.common.fetch;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ParameterEncoder {

   private final Map<String, Object> parameters;
   
   public ParameterEncoder() {
      this.parameters = new LinkedHashMap<String, Object>();
   }
   
   public void append(String name, int value) {
      parameters.put(name, value);
   }
   
   public void append(String name, long value) {
      parameters.put(name, value);
   }
   
   public void append(String name, boolean value) {
      parameters.put(name, value);
   }
   
   public void append(String name, double value) {
      parameters.put(name, value);
   }
   
   public void append(String name, String value) {
      parameters.put(name, value);
   }
   
   public String encode() throws Exception {
      Set<String> keys = parameters.keySet();
      
      if(!keys.isEmpty()) {
         StringBuilder builder = new StringBuilder();
         
         for(String name : keys) {
            Object value = parameters.get(name);
            String token = String.valueOf(value);
            String escape = URLEncoder.encode(token, "UTF-8");            
            int length = builder.length();
            
            if(length > 0) {
               builder.append("&");
            }
            builder.append(name);
            builder.append("=");
            builder.append(escape);
         }
         return builder.toString();
      }
      return "";
   }
}
