package com.authrus.attribute;

import java.util.Set;

public class CombinationBuilder implements ObjectBuilder {

   private final Set<ObjectBuilder> sequence;

   public CombinationBuilder(Set<ObjectBuilder> sequence) {
      this.sequence = sequence;
   }

   @Override
   public Object createInstance(Class type) {
      for (ObjectBuilder factory : sequence) {
         Object value = factory.createInstance(type);

         if (value != null) {
            return value;
         }
      }
      return null;
   }

}
