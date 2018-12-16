package com.authrus.tuple.grid;

import java.util.List;

public class SchemaDelta {

   private final List<Column> newColumns;
   private final Version version;

   public SchemaDelta(List<Column> newColumns, Version version) {
      this.newColumns = newColumns;
      this.version = version;
   }

   public Version getVersion() {
      return version;
   }

   public List<Column> getColumns() {
      return newColumns;
   }

   @Override
   public String toString() {
      return newColumns.toString();
   }
}
