package com.authrus.attribute;

import java.util.HashMap;
import java.util.Map;

public class AttributeConverter {
   
   private final AttributeSerializer serializer;

   public AttributeConverter(AttributeSerializer serializer) {
      this.serializer = serializer;
   }
   
   public Map<String, Object> fromObject(Object object) {
      Map<String, Object> map = new HashMap<String, Object>();
      MapWriter writer = new MapWriter(map);

      try {
         serializer.write(object, writer);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create message", e);
      }
      return map;
   }
   
   public Object toObject(Map<String, Object> row) {
      MapReader reader = new MapReader(row);

      try {
         return serializer.read(reader);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create object", e);
      }
   }
}
