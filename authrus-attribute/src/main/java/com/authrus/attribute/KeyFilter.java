package com.authrus.attribute;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class KeyFilter {

   private final Map<String, ?> attributes;
   private final Set<String> empty;
 
   public KeyFilter(Map<String, ?> attributes) {
      this.empty = Collections.emptySet();
      this.attributes = attributes;
   }

   public Iterator<String> readKeys(String name) {
      Set<String> keys = attributes.keySet();
      int length = name.length();

      if (!keys.isEmpty()) {
         Set<String> matches = new LinkedHashSet<String>();

         for (String key : keys) {
            if (key.startsWith(name)) {
               if (key.charAt(length) == '{') {
                  int terminate = key.indexOf('}', length);

                  if (terminate > length) {
                     String token = key.substring(length + 2, terminate - 1);

                     if (token != null) {
                        matches.add(token);
                     }
                  }
               }
            }
         }
         return matches.iterator();
      }
      return empty.iterator();
   }
}
