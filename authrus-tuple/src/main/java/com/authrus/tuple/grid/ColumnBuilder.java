package com.authrus.tuple.grid;

class ColumnBuilder implements IndexBuilder<Column> {

   @Override
   public Column createIndex(String name, Version version, int index) {
      return new Column(name, version, index);
   }
}
