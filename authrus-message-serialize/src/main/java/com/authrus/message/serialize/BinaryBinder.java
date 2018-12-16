package com.authrus.message.serialize;

import java.util.Map;

import com.authrus.message.bind.ObjectBinder;

public class BinaryBinder extends ObjectBinder {

   private final BinaryMarshaller marshaller;
   
   public BinaryBinder(String attribute) {
      this.marshaller = new BinaryMarshaller(attribute);
   }

   @Override
   public Map<String, Object> fromObject(Object object, String type) {
      return marshaller.fromObject(object);
   }

   @Override
   public Object toObject(Map<String, Object> row, String type) {
      return marshaller.toObject(row);
   }
}
