package com.authrus.common.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ListMap<K, V> extends LinkedHashMap<K, List<V>> {

   public ListMap() {
      super();
   }

   public ListMap(int capacity) {
      super(capacity);
   }

   public void add(K key, V value) {
      List<V> list = get(key);

      if(list == null) {
         list = new ArrayList<V>();
         put(key, list);
      }
      list.add(value);
   }

   public void set(K key, V value) {
      List<V> list = get(key);

      if(list != null) {
         list = new ArrayList<V>();
         put(key, list);
      }
      list.add(value);
   }

   public V getFirst(K key) {
      List<V> list = get(key);

      if(list == null) {
         return null;
      }
      if(list.isEmpty()) {
         return null;
      }
      return list.get(0);
   }
}
