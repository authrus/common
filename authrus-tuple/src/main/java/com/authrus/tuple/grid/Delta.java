package com.authrus.tuple.grid;

public class Delta {

   private final SchemaDelta schema;
   private final TableDelta table;
   private final String type;

   public Delta(SchemaDelta schema, TableDelta table, String type) {
      this.schema = schema;
      this.table = table;
      this.type = type;
   }

   public String getType() {
      return type;
   }

   public SchemaDelta getSchema() {
      return schema;
   }

   public TableDelta getTable() {
      return table;
   }

   @Override
   public String toString() {
      return String.format("%s: [%s] [%s]", type, schema, table);
   }
}
