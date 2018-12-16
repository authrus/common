package com.authrus.tuple.grid;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.authrus.predicate.Predicate;

class Table {
   
   private final TableUpdater updater;
   private final RowMatcher matcher;   
   private final RowMerger merger;
   private final TableModel model;
   private final Revision revision;
   private final Version version;
   private final Schema schema;

   public Table(Structure structure, Schema schema) {
      this(structure, schema, 20000);
   }
   
   public Table(Structure structure, Schema schema, int capacity) {
      this.version = new Version();
      this.revision = new Revision(version);
      this.model = new TableModel(revision, capacity);
      this.merger = new RowMerger(structure, schema);
      this.matcher = new RowMatcher(structure);
      this.updater = new TableUpdater(version);
      this.schema = schema;
   }
   
   public int countRows() {
      return model.countRows();
   }

   public boolean containsRow(String key) {
      return model.containsRow(key);
   }
   
   public Row selectRow(String key) {
      Row row = model.selectRow(key);
      
      if(row != null) {
         long creationTime = row.getTime();
         
         if(creationTime > 0) {
            return row;
         }
      }
      return null;
   }

   public List<Row> selectRows(Predicate predicate) {
      Iterator<Key> keys = model.listKeys();

      if (!keys.hasNext()) {
         List<Row> list = new ArrayList<Row>();
         
         while(keys.hasNext()) {
            Key key = keys.next();
            String name = key.getName();
            Row row = selectRow(name);
            
            if(row != null) {
               if (matcher.match(predicate, row, false)) {
                  list.add(row);
               }
            }
         }
         return list;
      }
      return Collections.emptyList();
   } 

   public Row insertRow(Row row) {
      String key = row.getKey();
      Row current = model.selectRow(key);
      Row merged = merger.merge(row, current);
      
      return updater.update(row, merged);
   }

   public TableDelta changeSince(Cursor cursor) {
      Version current = version.copy();
      RowAccessor accessor = model.listRows();
      Iterator<Key> keys = model.listKeys();
      KeyDelta keyDelta = model.changeSince(cursor);
      Predicate predicate = cursor.getPredicate();
      Version reference = cursor.getTableVersion();
      
      if (!model.isEmpty()) {
         List<RowDelta> changedRows = new LinkedList<RowDelta>();

         while(keys.hasNext()) {
            Key key = keys.next();
            long revision = key.getRevision();
            int offset = key.getIndex();

            if (offset >= 0) {
               Row row = accessor.get(offset); 
               
               if(row != null) { 
                  Version modified = row.getVersion();
                  boolean strict = changedRows.isEmpty();
                  long change = row.getChange();
                  long time = row.getTime();
                  long update = modified.get();
                  int count = row.getCount();
                  int columns = 0;
   
                  if(update >= revision) {
                     if (modified.after(reference) && matcher.match(predicate, row, strict)) {
                        List<Cell> newCells = new LinkedList<Cell>();
   
                        for (int i = 0; i < count; i++) {
                           Cell cell = row.getCell(i);
   
                           if (cell != null) {
                              Version created = cell.getVersion();
   
                              if (created.after(reference)) {
                                 newCells.add(cell);
                              }
                              columns++;
                           }
                        }
                        if (!newCells.isEmpty()) {
                           RowDelta delta = new RowDelta(schema, newCells, key, time, change, columns);
                           
                           if(change >= 0) {
                              changedRows.add(delta);
                           }
                        }
                     }
                  }
               }
            }
         }
         return new TableDelta(changedRows, keyDelta, current);
      }
      return new TableDelta(EMPTY_LIST, keyDelta, current);
   }

   private class TableUpdater {

      private final Version version;

      public TableUpdater(Version version) {
         this.version = version;
      }

      public synchronized Row update(Row newRow, Row mergedRow) {
         String key = mergedRow.getKey();
         Row currentRow = model.selectRow(key);

         if (currentRow != null) {
            long current = currentRow.getChange();
            long update = newRow.getChange();
            
            if(update == 0) {  
               long merge = mergedRow.getChange();
               long expect = current + 1;
               
               if (merge != expect) { // optimistic concurrency
                  mergedRow = merger.merge(newRow, currentRow);
               }
            } else {
               if(update > current) { // we have to merge again
                  mergedRow = merger.merge(newRow, currentRow);                 
               } else {
                  mergedRow = currentRow; // deny the update
               }
            }            
         } 
         if (mergedRow != currentRow) { // consider flushing current!!
            Version reference = mergedRow.getVersion();
            Version update = version.next();
            long value = update.get();

            reference.set(value);
            model.updateRow(mergedRow);
            version.update();
         }
         return mergedRow;
      }
   }
}
