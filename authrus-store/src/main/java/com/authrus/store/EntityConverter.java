package com.authrus.store;

import java.util.Map;

import lombok.AllArgsConstructor;

import com.zuooh.message.bind.ObjectMarshaller;
import com.zuooh.tuple.Tuple;

@AllArgsConstructor
public class EntityConverter {

   private final ObjectMarshaller marshaller;

   public Tuple fromEntity(Object object, Schema schema) {
      Map<String, Object> attributes = marshaller.fromObject(object);
      String entity = schema.getEntity();
      
      return new Tuple(attributes, entity);
   }
   
   public Object toEntity(Tuple tuple) {
      Map<String, Object> attributes = tuple.getAttributes();
      return marshaller.toObject(attributes);
   }
   
}
