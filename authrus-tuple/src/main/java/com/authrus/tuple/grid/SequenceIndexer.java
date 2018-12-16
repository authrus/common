package com.authrus.tuple.grid;

import java.util.Iterator;

class SequenceIndexer<T extends Index> implements Indexer<T> {

   private volatile SequenceAllocator allocator;
   private volatile IndexTable<T> indexes;

   public SequenceIndexer(IndexBuilder<T> builder, Version version) {
      this(builder, version, 5000);
   }
   
   public SequenceIndexer(IndexBuilder<T> builder, Version version, int capacity) {
      this.allocator = new SequenceAllocator(builder, version, capacity);
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
         value = allocator.allocate(name);
      }
      return value;
   }

   private class SequenceAllocator {

      private IndexBuilder<T> builder;
      private Version version;
      private int capacity;
      private int count;

      public SequenceAllocator(IndexBuilder<T> builder, Version version, int capacity) {
         this.capacity = capacity;
         this.builder = builder;
         this.version = version;
      }

      public synchronized T allocate(String name) {
         Version update = version.next();
         T current = indexes.get(name); // double check it does not exist 

         if (current == null) {
            if(count + 1 > capacity) {
               throw new IllegalStateException("Exceeded limit of " + capacity + " trying to create " + name);
            }
            T allocation = builder.createIndex(name, update, count++);

            if (allocation != null) {
               IndexTable<T> copy = new IndexTable<T>(capacity);

               if(!indexes.isEmpty()) {                 
                  for(T value : indexes) {
                     String key = value.getName();
                     
                     if(!key.equals(name)) {
                        copy.insert(key, value); 
                     }
                  }
               }
               copy.insert(name, allocation);
               indexes = copy;
               version.update();
            }
            return allocation; 
         }
         return current; // it was created by someone else
      }
   }
}
