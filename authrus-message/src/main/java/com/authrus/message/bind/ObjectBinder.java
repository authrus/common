package com.authrus.message.bind;

import java.util.Collections;
import java.util.Map;

public class ObjectBinder {

   private final Map<String, ObjectMarshaller> marshallers;

   public ObjectBinder() {
      this(Collections.EMPTY_MAP);
   }

   public ObjectBinder(Map<String, ObjectMarshaller> marshallers) {
      this.marshallers = marshallers;
   }

   public Map<String, Object> fromObject(Object object, String type) {
      ObjectMarshaller marshaller = marshallers.get(type);

      if (marshaller == null) {
         throw new IllegalArgumentException("No marshaller for '" + type + "'");
      }
      return marshaller.fromObject(object);
   }

   public Object toObject(Map<String, Object> row, String type) {
      ObjectMarshaller marshaller = marshallers.get(type);

      if (marshaller == null) {
         throw new IllegalArgumentException("No marshaller for '" + type + "'");
      }
      return marshaller.toObject(row);
   }
}
