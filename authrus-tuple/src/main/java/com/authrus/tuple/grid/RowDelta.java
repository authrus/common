package com.authrus.tuple.grid;

import java.util.List;

public class RowDelta {

   private final List<Cell> changes;
   private final Schema schema;
   private final Key key;
   private final long change;
   private final long time;
   private final int columns;

   public RowDelta(Schema schema, List<Cell> changes, Key key, long time, long change, int columns) {
      this.changes = changes;
      this.columns = columns;
      this.change = change;
      this.schema = schema;
      this.time = time;
      this.key = key;
   }   
   
   public long getTime() {
      return time;
   }

   public long getChange() {
      return change;
   }

   public int getColumns() {
      return columns;
   }

   public List<Cell> getChanges() {
      return changes;
   }

   public Schema getSchema() {
      return schema;
   }

   public Key getKey() {
      return key;
   }

   @Override
   public String toString() {
      return String.format("%s %s (%s)", key, change, changes);
   }
}
