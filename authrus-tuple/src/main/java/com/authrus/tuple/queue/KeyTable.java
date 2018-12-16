package com.authrus.tuple.queue;

import java.util.HashMap;
import java.util.Map;

class KeyTable {

   private volatile Map<String, String>  references;
   private volatile KeyAllocator allocator;
   private volatile int capacity;
   
   public KeyTable() {
      this(10000);
   }
   
   public KeyTable(int capacity) {
      this.references = new HashMap<String, String>();
      this.allocator = new KeyAllocator();
      this.capacity = capacity;
   }
   
   public String copy(String key) {
      String copy = references.get(key);
      
      if(copy == null) {
         int size = references.size();
      
         if(size < capacity) {
            return allocator.allocate(key);
         }
         return key;
      }
      return copy;
   }
   
   private class KeyAllocator {

      public synchronized String allocate(String key) {
         String copy = references.get(key);
         
         if(copy == null) {
            Map<String, String> update = new HashMap<String, String>(references);
         
            update.put(key, key);
            references = update;
            copy = key;
         }
         return copy;
      }
   }
}
