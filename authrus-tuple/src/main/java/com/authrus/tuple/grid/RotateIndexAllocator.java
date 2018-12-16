package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;
 
class RotateIndexAllocator<T extends Index> {
   
   private Map<Integer, T> indexes;
   private IndexBuilder<T> builder;
   private int section;
   private int offset;
  
   public RotateIndexAllocator(IndexBuilder<T> builder, int section) {
      this.indexes = new HashMap<Integer, T>();
      this.section = section;
      this.builder = builder;
   }
  
   public synchronized RotateIndex<T> allocate(String name, Version version) { 
      int total = section * 3;
      int fresh = section * 2;
      int index = offset++;
     
      if(offset >= total) {
         offset = 0;
      }          
      T value = builder.createIndex(name, version, index);           
      int delete = index - fresh;
      
      if(delete < 0) {// do we need to wrap around
         delete = total - -delete;
      }    
      T previous = indexes.remove(delete); // remove old indexes
      
      if(value != null) {
         indexes.put(index, value);
      }
      return new RotateIndex<T>(value, previous);
   }
}
