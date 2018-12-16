package com.authrus.attribute;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class PrefixFilter {
   
   private final Map<String, ?> attributes;

   public PrefixFilter(Map<String, ?> attributes) {
      this.attributes = attributes;
   }

   public Iterator<String> readChildren(String name) {
      Set<String> keys = attributes.keySet();
      Iterator<String> iterator = keys.iterator();
      
      return new PrefixIterator(iterator, name);
   }
   
   private class PrefixIterator implements Iterator<String> {
      
      private final AtomicReference<String> result;
      private final Iterator<String> iterator;
      private final String name;
      
      public PrefixIterator(Iterator<String> iterator, String name) {
         this.result = new AtomicReference<String>();
         this.iterator = iterator;
         this.name = name;         
      }

      @Override
      public boolean hasNext() {
         String value = result.get();
         
         if(value != null) {
            return true;
         }
         while(iterator.hasNext()) {
            String key = iterator.next();
            
            if (!key.equals(name) && key.startsWith(name)) {
               int length = name.length();
               char next = key.charAt(length);
               
               if (next == '{' || next == '[' || next == '.') { 
                  result.set(key);
                  return true;
               }
            }
         }
         return false;
      }

      @Override
      public String next() {
         String value = result.get();
         
         if(value == null) {
            if(hasNext()) {
               value = result.get();
            }
         }         
         if(value != null) {
            int length = name.length();
            char next = value.charAt(length);
            
            if (next == '{' || next == '[') { 
               value = value.substring(length);
            } else if(next == '.') {
               value = value.substring(length + 1);
            }
            result.set(null);
         }
         return value;
      }

      @Override
      public void remove() {
         iterator.remove();
      }      
   }
}
