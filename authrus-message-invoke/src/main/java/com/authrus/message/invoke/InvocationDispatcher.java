package com.authrus.message.invoke;

import static com.authrus.message.invoke.ReturnStatus.EXCEPTION;
import static com.authrus.message.invoke.ReturnStatus.SUCCESS;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class InvocationDispatcher {

   private final Map<Class, Object> destinations;
   private final Map<Object, Method> methods;
   private final Map<Class, Object> cache;
   private final Set<Object> failures;
   private final SignatureBuilder builder;

   public InvocationDispatcher(Map<Class, Object> destinations) {
      this.destinations = new ConcurrentHashMap<Class, Object>(destinations);
      this.methods = new ConcurrentHashMap<Object, Method>();
      this.cache = new ConcurrentHashMap<Class, Object>();
      this.failures = new CopyOnWriteArraySet<Object>();
      this.builder = new SignatureBuilder();
   }

   public ReturnValue dispatch(Invocation invocation) throws Exception {
      Object[] params = invocation.getArguments();
      String name = invocation.getSignature();

      try {
         Object value = resolve(invocation);
         Method method = match(invocation);

         if (method == null || value == null) {
            throw new IllegalArgumentException("No method found named " + name);
         }
         Object result = method.invoke(value, params);

         if (result != null) {
            if (!Serializable.class.isInstance(result)) {
               throw new IllegalStateException("Return value is not serializable for " + method);
            }
         }
         return new ReturnValue(SUCCESS, result);
      } catch (Exception cause) {
         return new ReturnValue(EXCEPTION, cause);
      }
   }

   private Object resolve(Invocation invocation) throws Exception {
      Class require = invocation.getType();

      if (!cache.containsKey(require) && !failures.contains(require)) {
         Set<Class> types = destinations.keySet();

         for (Class entry : types) {
            Object value = destinations.get(entry);

            while (entry != null) {
               if (entry == require) {
                  cache.put(require, value);
                  return value;
               }
               Class[] interfaces = entry.getInterfaces();

               for (Class item : interfaces) {
                  if (item == require) {
                     cache.put(require, value);
                     return value;
                  }
               }
               entry = entry.getSuperclass();
            }
         }
         failures.add(require);
      }
      return cache.get(require);
   }

   private Method match(Invocation invocation) throws Exception {
      Class type = invocation.getType();
      String signature = invocation.getSignature();

      if (destinations.containsKey(type) && !failures.contains(signature)) {
         Method method = methods.get(signature);

         if (method == null) {
            Method[] list = type.getDeclaredMethods();

            for (int i = 0; i < list.length; i++) {
               String key = builder.createSignature(type, list[i]);

               if (!list[i].isAccessible()) {
                  list[i].setAccessible(true);
               }
               methods.put(key, list[i]);
            }
         }
         Method resolved = methods.get(signature);

         if (resolved == null) {
            failures.add(signature);
         }
         return resolved;
      }
      return null;
   }
}
