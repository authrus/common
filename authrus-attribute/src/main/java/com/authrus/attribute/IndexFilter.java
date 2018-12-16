package com.authrus.attribute;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class IndexFilter {

   private final Map<String, ?> attributes;
   private final Set<Integer> empty;

   public IndexFilter(Map<String, ?> attributes) {
      this.empty = Collections.emptySet();
      this.attributes = attributes;
   }

   public Iterator<Integer> readIndexes(String name) {
      Set<String> keys = attributes.keySet();
      int length = name.length();

      if (!keys.isEmpty()) {
         Set<Integer> matches = new TreeSet<Integer>();

         for (String key : keys) {
            if (key.startsWith(name)) {
               if (key.charAt(length) == '[') {
                  int terminate = key.indexOf(']', length);

                  if (terminate > length) {
                     String token = key.substring(length + 1, terminate);
                     Integer integer = Integer.parseInt(token);

                     matches.add(integer);
                  }
               }
            }
         }
         return matches.iterator();
      }
      return empty.iterator();
   }
}
