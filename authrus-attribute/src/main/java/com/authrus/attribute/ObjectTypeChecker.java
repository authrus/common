package com.authrus.attribute;

import java.util.Collection;
import java.util.Map;

import com.authrus.attribute.transform.ObjectTransformer;

public class ObjectTypeChecker {

   private final ObjectTransformer transformer;
   private final PrimitiveConverter converter;
   
   public ObjectTypeChecker(ObjectTransformer transformer) {
      this.converter = new PrimitiveConverter();
      this.transformer = transformer;
   }   
   
   public boolean isArray(Class type) {
      return type.isArray();
   }
   
   public boolean isMap(Class type) {
      return Map.class.isAssignableFrom(type);
   }
   
   public boolean isCollection(Class type) {
      return Collection.class.isAssignableFrom(type);
   }

   public boolean isTransform(Class type) {
      return transformer.valid(type);
   }

   public boolean isPrimitive(Class type) {
      Class actual = converter.convert(type);

      if (actual == String.class) {
         return true;
      }
      if (actual == Integer.class) {
         return true;
      }
      if (actual == Double.class) {
         return true;
      }
      if (actual == Float.class) {
         return true;
      }
      if (actual == Boolean.class) {
         return true;
      }
      if (actual == Byte.class) {
         return true;
      }
      if (actual == Short.class) {
         return true;
      }
      if (actual == Long.class) {
         return true;
      }
      if (actual == Character.class) {
         return true;
      }
      if (Enum.class.isAssignableFrom(actual)) {
         return true;
      }
      return false;
   }
}
