package com.authrus.message.serialize;

import java.util.Map;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.message.bind.ObjectBinder;

public class AttributeBinder extends ObjectBinder {

   private final AttributeMarshaller marshaller;
   
   public AttributeBinder(AttributeSerializer serializer) {
      this.marshaller = new AttributeMarshaller(serializer);
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
