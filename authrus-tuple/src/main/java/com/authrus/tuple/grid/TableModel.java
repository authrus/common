package com.authrus.tuple.grid;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

class TableModel {

   private final KeyAllocator allocator;
   private final RowUpdater updater;

   public TableModel(Revision revision) {
      this(revision, 20000);
   }
   
   public TableModel(Revision revision, int capacity) {
      this.allocator = new KeyAllocator(revision, capacity);
      this.updater = new RowUpdater();
   }

   public boolean isEmpty() {
      return updater.rows.length <= 0;
   }
   
   public int countRows() {
      return updater.rows.length;
   }    

   public boolean containsRow(String key) {
      return allocator.containsKey(key);
   }

   public Row selectRow(String name) {
      if(allocator.containsKey(name)) { 
         Key key = allocator.getKey(name);
         int offset = key.getIndex();
   
         if (offset < updater.rows.length) {
            return updater.rows.get(offset);
         }
      }
      return null;
   }

   public Iterator<Key> listKeys() {
      return allocator.getKeys();
   }

   public RowAccessor listRows() {
      return updater.rows;
   }

   public Key updateRow(Row row) {
      String name = row.getKey();
      Key key = allocator.getKey(name);
      int offset = key.getIndex(); // this may be a new index

      if (offset >= 0) {
         updater.update(row, offset);
      }
      return key;  
   }

   public KeyDelta changeSince(Cursor cursor) {
      Version version = cursor.getKeyVersion();

      if (version != null) {
         return allocator.changeSince(version);
      }
      return null;
   }
   
   private class RowArray implements RowAccessor {
    
      private final AtomicReferenceArray<Row> rows;
      private final int length;

      public RowArray(int length) {
         this.rows = new AtomicReferenceArray<Row>(length);
         this.length = length;
      }
      
      @Override
      public Row get(int offset) {
         if(offset >= length) {
            return null;
         }
         return rows.get(offset);
      }
      
      public void set(int offset, Row row) {
         rows.set(offset, row);         
      }
      
      public RowArray copy(int size) {
         RowArray copy  = new RowArray(size);
         
         for(int i = 0; i < Math.min(size, length); i++) {            
            Row row = rows.get(i);
            
            if(row != null) {
               copy.rows.set(i, row);
            }
         }
         return copy;
      }
      
      public int length() {
         return length;
      }
   }

   private class RowUpdater {

      private volatile RowArray rows;

      public RowUpdater() {
         this.rows = new RowArray(0);
      }

      public synchronized void update(Row row, int offset) {
         int length = rows.length();
         
         if (offset >= length) {
            RowArray copy = rows.copy(offset + 1);

            copy.set(offset, row); // must set before switch
            rows = copy;
         } else {
            rows.set(offset, row);
         }
      } 
   }
}
