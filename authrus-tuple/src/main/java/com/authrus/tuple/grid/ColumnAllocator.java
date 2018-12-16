package com.authrus.tuple.grid;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ColumnAllocator implements Schema {

   private final Indexer<Column> indexer;
   private final ColumnBuilder builder;
   private final Version version;
   
   public ColumnAllocator(Structure structure) {
      this(structure, 5000);
   }
   
   public ColumnAllocator(Structure structure, int capacity) {
      this.builder = new ColumnBuilder();
      this.version = new Version();
      this.indexer = new SequenceIndexer<Column>(builder, version, capacity);
   }

   @Override
   public int getCount() {
      return indexer.size();
   }

   @Override
   public Column getColumn(String name) {
      return indexer.index(name);
   }
   
   @Override
   public Column getColumn(int index) {
      Iterator<Column> columns = indexer.iterator();

      while(columns.hasNext()) {
         Column column = columns.next();
         int value = column.getIndex();
         
         if(value == index) {
            return column;
         }
      }
      return null;
   }

   public SchemaDelta changeSince(Cursor cursor) {
      Version current = version.copy();
      Version reference = cursor.getSchemaVersion();
      Iterator<Column> columns = indexer.iterator();

      if (!indexer.isEmpty()) {
         List<Column> changes = new ArrayList<Column>();

         while(columns.hasNext()) {
            Column column = columns.next();
            Version created = column.getVersion();

            if (created.after(reference)) {
               changes.add(column);
            }
         }
         return new SchemaDelta(changes, current);
      }
      return new SchemaDelta(EMPTY_LIST, current);
   }
}
