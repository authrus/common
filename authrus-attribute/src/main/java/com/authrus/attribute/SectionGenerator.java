package com.authrus.attribute;

import java.util.Map;

import com.authrus.common.collections.LeastRecentlyUsedMap;

public class SectionGenerator {

   private volatile StringConverter converter;
   private volatile Map<Object, String> keys;
   private volatile KeyGenerator generator;
   private volatile String[] indexes;

   public SectionGenerator(ClassResolver resolver) {
      this(resolver, 5000);
   }

   public SectionGenerator(ClassResolver resolver, int capacity) {
      this.keys = new LeastRecentlyUsedMap<Object, String>(capacity);
      this.converter = new StringConverter(resolver);
      this.generator = new KeyGenerator(capacity);
      this.indexes = new String[capacity];
   }
   
   public Object generateValue(Class type, String text) throws Exception {
      if(text != null) {
         return converter.convert(type, text);
      }
      return null;
   }

   public String generateKey(Object key) throws Exception {
      String value = keys.get(key);

      if (value == null) {
         return generator.generate(key);
      }
      return value;
   }

   public String generateIndex(Integer index) throws Exception {
      if (index >= indexes.length) {
         return "[" + index + "]";
      }
      String value = indexes[index];

      if (value == null) {
         return indexes[index] = "[" + index + "]";
      }
      return value;
   }
   
   private class KeyGenerator {
      
      private final int capacity;
      
      public KeyGenerator(int capacity) {
         this.capacity = capacity;
      }
      
      public synchronized String generate(Object key) throws Exception {
         String value = create(key);
         
         if(value != null) {
            Map<Object, String> copy = new LeastRecentlyUsedMap<Object, String>(capacity);

            copy.putAll(keys);
            copy.put(key, value);
            keys = copy;
         }
         return value;
      }
      
      private synchronized String create(Object key) throws Exception {
         String value = keys.get(key);
         
         if(value == null) {
            StringBuilder builder = new StringBuilder();
            
            builder.append("{'");
            builder.append(key);
            builder.append("'}");
            
            return builder.toString();
         }
         return value;
      }
   }
}
