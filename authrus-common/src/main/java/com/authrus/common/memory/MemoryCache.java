package com.authrus.common.memory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryCache<V> {  

   private final MemoryStore<V> memoryStore;
   private final MemoryMap<V> memoryMap;   

   public MemoryCache(MemoryStore<V> memoryStorage) {
      this.memoryMap = new MemoryMap<V>(memoryStorage);
      this.memoryStore = memoryStorage;
   }
   
   public synchronized int usedMemory() {
      return memoryMap.usedMemory();
   }
   
   public synchronized Set<String> keySet() {
      Set<String> keys = memoryMap.keySet();
      
      if(!keys.isEmpty()) {
         return new HashSet<String>(keys);
      }
      return Collections.emptySet();
   }
   
   public synchronized boolean contains(String resource) {
      V value = memoryMap.get(resource);
      
      if(value == null) {
         return memoryStore.exists(resource);
      }
      return true;
   }
   
   public synchronized V get(String resource) {
      V image = memoryMap.get(resource);

      if(image == null) {
         return load(resource);
      } else {
         if(memoryStore.stale(image, resource)) {
            remove(resource);
            return load(resource);
         }
      }
      return image;
   }

   private synchronized V load(String resource) {
      if(memoryStore.exists(resource)) {
         V image = memoryStore.read(resource);

         if(image != null) {
            if(!memoryMap.save(resource, image)) {
               V existing = memoryMap.get(resource);
               
               if(existing != null) {
                  memoryStore.dispose(image, resource);
                  return existing;
               }
               if(!memoryMap.save(resource, image)) {
                  throw new IllegalStateException("Tried to save " + image + " but cache says it is already saved");
               }               
            }
         }
         return image;
      }
      return null;
   }
   
   public synchronized boolean put(String key, V value) {
      return memoryMap.save(key, value);
   }
   
   public synchronized void remove(String key) {
      V value = memoryMap.delete(key);
      
      if(value != null) {
         memoryStore.dispose(value, key);
      }
   }
  
   public synchronized boolean isEmpty() {
      return memoryMap.isEmpty();
   }
   
   public synchronized int size() {
      return memoryMap.size();
   }

   @Override
   public synchronized String toString() {
      StringBuilder builder = new StringBuilder();

      if(!memoryMap.isEmpty()) {
         Runtime runtime = Runtime.getRuntime();
         float maxMemory = runtime.maxMemory();
         float usedMemory = memoryMap.usedMemory();
         float fractionOfMemory = usedMemory / maxMemory;
         int percentOfMemory = Math.round(fractionOfMemory * 100.0f);
         int countOfItems = memoryMap.size();
         
         return String.format("cache has ", countOfItems, " and occupies ", usedMemory, " bytes which is ", percentOfMemory, "% of maximum");
      }
      return builder.toString();
   }

   private static class MemoryMap<V> {

      private final ConcurrentMap<String, V> memoryMap;
      private final MemoryStore<V> memoryStore;
      private final AtomicInteger usedMemory;

      public MemoryMap(MemoryStore<V> memoryStore) {
         this.memoryMap = new ConcurrentHashMap<String, V>();
         this.usedMemory = new AtomicInteger();
         this.memoryStore = memoryStore;
      }
      
      public Set<String> keySet() {
         return memoryMap.keySet();
      }

      public int usedMemory() {
         return usedMemory.get();
      }      
      
      public boolean isEmpty() {
         return memoryMap.isEmpty();
      }
      
      public V get(String key) {
         return memoryMap.get(key);
      }

      public V delete(String key) {
         V value = memoryMap.remove(key);

         if(value != null) {
            int size = memoryStore.sizeOf(value, key);

            if(size > 0) {
               usedMemory.addAndGet(-size);
            }
         }
         return value;
      }

      public boolean save(String key, V value) {
         int size = memoryStore.sizeOf(value, key);

         if(size > 0) {
            usedMemory.addAndGet(size);
         }
         V previous = memoryMap.putIfAbsent(key, value);

         if(previous != null) {
            return false;
         }
         return true;
      }      
      
      public int size() {
         return memoryMap.size();
      }
      
      @Override
      public String toString() {
         return memoryMap.toString();
      }
      
   }
}
