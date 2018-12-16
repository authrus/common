package com.authrus.attribute;

import java.util.Map;

import com.authrus.common.collections.LeastRecentlyUsedMap;

public class ClassResolver {

   private volatile Map<String, Class> cache;
   private volatile ClassAllocator allocator;

   public ClassResolver() {
      this(10000);
   }
   
   public ClassResolver(int capacity) {
      this.cache = new LeastRecentlyUsedMap<String, Class>(capacity);
      this.allocator = new ClassAllocator(capacity);
   }

   public Class resolveClass(String name) throws Exception {
      Class type = cache.get(name);

      if (type == null) {
         return allocator.allocate(name);
      }
      return type;
   }    
   
   private class ClassAllocator {
      
      private final int capacity;
      
      public ClassAllocator(int capacity) {
         this.capacity = capacity;
      }
      
      public synchronized Class allocate(String name) throws Exception {
         Class type = load(name);
         
         if(type != null) {
            Map<String, Class> copy = new LeastRecentlyUsedMap<String, Class>(capacity);
         
            copy.putAll(cache);
            copy.put(name, type);
            cache = copy;
         }
         return type;
      }
      
      private synchronized Class load(String name) throws Exception {
         Class type = cache.get(name);
         
         if(type == null) {
            Thread thread = Thread.currentThread();
            ClassLoader loader = thread.getContextClassLoader();
            Class caller = getClass();
   
            if (loader == null) {
               loader = caller.getClassLoader();
            }
            return loader.loadClass(name);
         }
         return type;
      }
   }
}
