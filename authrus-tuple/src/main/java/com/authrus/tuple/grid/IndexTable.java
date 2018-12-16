package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class IndexTable<T extends Index> implements Iterable<T> {
   
   private final Map<String, T> indexes;
   private final List<T> values;
   
   public IndexTable(int capacity) {
      this.indexes = new HashMap<String, T>();
      this.values = new ArrayList<T>();
   }

   public T get(String name) {
      return indexes.get(name);
   }
   
   public Iterator<T> iterator() {
      return values.iterator();
   } 
   
   public boolean contains(String name) {
      return indexes.containsKey(name);
   }
   
   public void insert(String name, T value) {
      indexes.put(name, value);
      values.add(value);
   }  
   
   public boolean isEmpty() {
      return indexes.isEmpty();
   }
   
   public int size() {
      return indexes.size();
   }   
}
