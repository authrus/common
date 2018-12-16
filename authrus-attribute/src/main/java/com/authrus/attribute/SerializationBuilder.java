package com.authrus.attribute;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SerializationBuilder implements ObjectBuilder {

   private final Map<Class, MethodInvoker> cache;
   private final Set<Class> failures;

   public SerializationBuilder() {
      this.cache = new ConcurrentHashMap<Class, MethodInvoker>();
      this.failures = new CopyOnWriteArraySet<Class>();
   }

   public Object createInstance(Class type) {
      if (!failures.contains(type)) {
         MethodInvoker invoker = createInvoker(type);

         if (invoker == null) {
            failures.add(type);
         } else {
            try {
               return invoker.invoke(type);
            } catch (Exception e) {
               failures.add(type);
            }
         }
      }
      return null;
   }

   private MethodInvoker createInvoker(Class type) {
      MethodInvoker invoker = cache.get(type);

      if (invoker == null) {
         invoker = createInvoker(type, "newInstance");

         if (invoker == null) {
            invoker = createInvoker(type, "newInstance", Class.class);
         }
         if (invoker != null) {
            cache.put(type, invoker);
         }
      }
      return invoker;
   }

   private MethodInvoker createInvoker(Class type, String name, Class... list) {
      ObjectStreamClass factory = ObjectStreamClass.lookup(type);

      if (factory != null) {
         try {
            Method method = ObjectStreamClass.class.getDeclaredMethod(name, list);

            if (method != null) {
               if (!method.isAccessible()) {
                  method.setAccessible(true);
               }
               return new MethodInvoker(factory, method, list.length);
            }
         } catch (Exception e) {
            return null;
         }
      }
      return null;
   }

   private static class MethodInvoker {

      private final ObjectStreamClass factory;
      private final Method method;
      private final int parameters;

      public MethodInvoker(ObjectStreamClass factory, Method method, int parameters) {
         this.parameters = parameters;
         this.factory = factory;
         this.method = method;
      }

      public Object invoke(Class type) throws Exception {
         if (parameters > 0) {
            return method.invoke(factory, type);
         }
         return method.invoke(factory);
      }
   }
}
