package com.authrus.tuple.grid;

class CellCombiner {

   private final Schema schema;

   public CellCombiner(Schema schema) {
      this.schema = schema;
   }

   public String combineCells(Cell[] cells, String[] names) {
      if(names.length > 1) {
         StringBuilder builder = new StringBuilder();
   
         for (int i = 0; i < names.length; i++) {
            String value = convertCell(cells, names[i]);
   
            if (i > 0) {
               builder.append(".");
            }
            builder.append(value);
         }
         return builder.toString();
      }
      return convertCell(cells, names[0]);
   }
   
   private String convertCell(Cell[] cells, String name) {
      Column column = schema.getColumn(name);

      if (column == null) {
         throw new IllegalStateException("Schema does not contain index column " + name);
      }
      int position = column.getIndex();

      if (cells.length <= position) {
         throw new IllegalStateException("Row length is " + cells.length + " yet an index column is at " + position);
      }
      if (cells[position] == null) {
         throw new IllegalStateException("Row does not contain index column " + name);
      }
      Cell cell = cells[position];
      Object value = cell.getValue();
      
      return String.valueOf(value);
   }
}
