package com.authrus.message.bind;

import java.util.Map;

public class IdentityBinder extends ObjectBinder {
   
   private final IdentityMarshaller marshaller;
   
   public IdentityBinder() {
      this.marshaller = new IdentityMarshaller();
   }

   @Override
   public Map<String, Object> fromObject(Object object, String type) {
      return marshaller.fromObject((Map<String, Object>)object);
   }

   @Override
   public Object toObject(Map<String, Object> row, String type) {
      return marshaller.toObject(row);
   }
}
