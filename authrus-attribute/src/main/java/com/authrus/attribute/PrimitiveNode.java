package com.authrus.attribute;

import java.util.Collections;
import java.util.List;

public class PrimitiveNode implements ObjectNode {

   private final Class type;

   public PrimitiveNode(Class type) {
      this.type = type;
   }

   @Override
   public boolean isTransform() {
      return false;
   }

   @Override
   public boolean isPrimitive() {
      return true;
   }

   @Override
   public List<FieldAccessor> getFields() {
      return Collections.emptyList();
   }

   @Override
   public Converter getConverter(ObjectGraph graph) {
      return null;
   }

   @Override
   public Object getInstance() {
      return null;
   }

   @Override
   public Object getInstance(Class type) {
      return null;
   }

   @Override
   public Class getType() {
      return type;
   }
}
