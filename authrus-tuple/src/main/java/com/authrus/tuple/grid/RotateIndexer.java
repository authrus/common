package com.authrus.tuple.grid;

import java.util.Iterator;

class RotateIndexer<T extends Index> implements Indexer<T> {

   private volatile RotateIndexAllocator<T> allocator;
   private volatile RotateUpdater updater;
   private volatile IndexTable<T> indexes;

   public RotateIndexer(IndexBuilder<T> builder, Version version) {
      this(builder, version, 20000);
   }

   public RotateIndexer(IndexBuilder<T> builder, Version version, int capacity) {
      this.allocator = new RotateIndexAllocator<T>(builder, capacity);
      this.updater = new RotateUpdater(allocator, version, capacity);
      this.indexes = new IndexTable<T>(capacity);
   }

   @Override
   public boolean isEmpty() {
      return indexes.isEmpty();
   }

   @Override
   public int size() {
      return indexes.size();
   }

   @Override
   public Iterator<T> iterator() {
      return indexes.iterator();
   }

   @Override
   public boolean contains(String name) {
      return indexes.contains(name);
   }

   @Override
   public T index(String name) {
      T value = indexes.get(name);

      if (value == null) {
         value = updater.update(name);
      }
      return value;
   }

   private class RotateUpdater {

      private RotateIndexAllocator<T> allocator;
      private Version version;
      private int capacity;

      public RotateUpdater(RotateIndexAllocator<T> allocator, Version version, int capacity) {
         this.allocator = allocator;
         this.capacity = capacity;
         this.version = version;
      }

      public synchronized T update(String name) {
         Version next = version.next();
         T current = indexes.get(name); 

         if(current == null) {
            RotateIndex<T> result = allocator.allocate(name, next);
            T allocate = result.getAllocate();
            T delete = result.getDelete();
            
            if(allocate != null) {            
               IndexTable<T> copy = new IndexTable<T>(capacity);

               if(!indexes.isEmpty()) {                 
                  for(T value : indexes) {
                     String key = value.getName();
                     
                     if(value != delete) { // remove deleted key
                        if(!key.equals(name)) {
                           copy.insert(key, value);                       
                        }
                     } 
                  }
               }
               copy.insert(name, allocate);
               indexes = copy;
               version.update();               
            }
            return allocate;
         }
         return current;
      }
   }
}
