package com.authrus.tuple.grid;

import java.util.List;

public class TableDelta {

   private final List<RowDelta> changedRows;
   private final KeyDelta keyDelta;
   private final Version version;

   public TableDelta(List<RowDelta> changedRows, KeyDelta keyDelta, Version version) {
      this.changedRows = changedRows;
      this.keyDelta = keyDelta;
      this.version = version;
   }

   public Version getVersion() {
      return version;
   }

   public KeyDelta getKeys() {
      return keyDelta;
   }

   public List<RowDelta> getChanges() {
      return changedRows;
   }

   @Override
   public String toString() {
      return changedRows.toString();
   }
}
