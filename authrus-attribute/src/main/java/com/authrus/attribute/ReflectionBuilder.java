package com.authrus.attribute;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReflectionBuilder implements ObjectBuilder {

   private final Set<Class> failures;

   public ReflectionBuilder() {
      this.failures = new CopyOnWriteArraySet<Class>();
   }

   @Override
   public Object createInstance(Class type) {
      if (!failures.contains(type)) {
         try {
            return type.newInstance();
         } catch (Exception e) {
            failures.add(type);
         }
      }
      return null;
   }

}
