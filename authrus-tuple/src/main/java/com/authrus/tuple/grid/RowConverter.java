package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.authrus.tuple.Tuple;

class RowConverter {

   private final CellCombiner combiner;
   private final Structure structure;
   private final Schema schema;
   private final String type;

   public RowConverter(Structure structure, Schema schema, String type) {
      this.combiner = new CellCombiner(schema);
      this.structure = structure;
      this.schema = schema;
      this.type = type;
   }

   public Tuple fromRow(Row row) {
      Cell[] cells = row.getCells();
      long change = row.getChange();

      if (cells != null && cells.length > 0) {
         Map<String, Object> data = new HashMap<String, Object>();

         for (Cell cell : cells) {
            if (cell != null) {
               String name = cell.getColumn();
               Object value = cell.getValue();

               if (value != null) {
                  data.put(name, value);
               }
            }
         }
         return new Tuple(data, type, change);
      }
      return null;
   }

   public Row toRow(Tuple tuple) {
      Map<String, Object> data = tuple.getAttributes();
      String[] constants = structure.getConstants();
      String[] key = structure.getKey();
      Set<String> names = data.keySet();
      long change = tuple.getChange();
      int size = data.size();

      for (String name : constants) {
         if (!data.containsKey(name)) {
            throw new IllegalStateException("Constant attribute '" + name + "' missing for '" + type + "'");
         }
      }
      String[] columns = new String[size];
      Object[] values = new Object[size];
      int[] indexes = new int[size];
      int indexPos = 0;

      for (String name : names) {
         Column column = schema.getColumn(name);
         Object value = data.get(name);
         int index = column.getIndex();

         columns[indexPos] = name;
         values[indexPos] = value;
         indexes[indexPos++] = index;
      }
      int width = schema.getCount();

      if (width > 0) {
         Cell[] cells = new Cell[width];
         Version version = new Version();     

         for (int i = 0; i < indexPos; i++) {
            Object value = values[i];
            String column = columns[i];
            int index = indexes[i];

            cells[index] = new Cell(column, value, version);
         }
         String result = combiner.combineCells(cells, key);
         long time = System.currentTimeMillis();

         return new Row(schema, version, cells, result, time, change);
      }
      return null;
   }
}
