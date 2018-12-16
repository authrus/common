package com.authrus.common.text;

import java.util.Map;
import java.util.Set;

import com.authrus.common.collections.LeastRecentlyUsedMap;

public class RegularExpressionFilter {
   
   private volatile Map<String, Boolean> matches;
   private volatile FilterMatchAllocator filter;
   
   public RegularExpressionFilter(Set<String> patterns) {
      this(patterns, false);
   }
   
   public RegularExpressionFilter(Set<String> patterns, boolean allow) {
      this(patterns, allow, 10000);
   }
   
   public RegularExpressionFilter(Set<String> patterns, boolean allow, int capacity) {
      this.matches = new LeastRecentlyUsedMap<String, Boolean>(capacity);
      this.filter = new FilterMatchAllocator(patterns, allow, capacity);
   }
  
   public boolean accept(String token) {
      Boolean match = matches.get(token);
      
      if(match == null) {
         return filter.accept(token);
      }
      return match.booleanValue();
   }
   
   private class FilterMatchAllocator {
      
      private final Set<String> patterns;
      private final boolean allow;
      private final int capacity;
      
      public FilterMatchAllocator(Set<String> patterns, boolean allow, int capacity) {
         this.patterns = patterns;
         this.capacity = capacity;
         this.allow = allow;
      }
      
      public synchronized boolean accept(String type) {
         Boolean value = matches.get(type);
         
         if(value == null) {
            Map<String, Boolean> copy = new LeastRecentlyUsedMap<String, Boolean>(capacity);
            
            copy.putAll(matches);
            
            if(matches(type)) {
               copy.put(type, allow);
            } else {
               copy.put(type, !allow);
            }
            matches = copy;
         }
         return matches.get(type);         
      }
      
      private synchronized boolean matches(String type) {                 
         for(String pattern : patterns) {
            if(type.equals(pattern) || type.matches(pattern)) {
               return true;
            }
         }
         return false;
      }
   }
}
