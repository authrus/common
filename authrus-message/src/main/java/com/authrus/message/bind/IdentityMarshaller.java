package com.authrus.message.bind;

import java.util.Map;

public class IdentityMarshaller implements ObjectMarshaller<Map<String, Object>> {
   
   public Map<String, Object> fromObject(Map<String, Object> object) {
      return object; 
   }
   
   public Map<String, Object> toObject(Map<String, Object> message) {
      return message;
   }
}
