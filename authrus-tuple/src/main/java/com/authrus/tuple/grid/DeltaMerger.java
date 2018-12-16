package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DeltaMerger {

   private Map<Integer, Row> rows;
   private Version version;

   public DeltaMerger() {
      this.rows = new HashMap<Integer, Row>();
      this.version = new Version();
   }

   public List<DeltaMerge> mergeTableDelta(TableDelta tableDelta) {
      List<RowDelta> rowChanges = tableDelta.getChanges();
      int count = rowChanges.size();
      
      if (count > 0) {
         List<DeltaMerge> changedRows = new ArrayList<DeltaMerge>(count);

         for (RowDelta rowDelta : rowChanges) {
            DeltaMerge row = mergeRowDelta(rowDelta);

            if (row != null) {
               changedRows.add(row);
            }
         }
         return changedRows;
      }
      return Collections.emptyList();
   }

   public DeltaMerge mergeRowDelta(RowDelta rowDelta) {
      List<Cell> cells = rowDelta.getChanges();
      Schema schema = rowDelta.getSchema();
      Key key = rowDelta.getKey();
      long time = rowDelta.getTime();
      long change = rowDelta.getChange();
      int width = schema.getCount();

      if (width > 0) {
         Cell[] newCells = new Cell[width];

         for (Cell cell : cells) {
            String name = cell.getColumn();
            Column column = schema.getColumn(name);
            int position = column.getIndex();

            if (position >= newCells.length) {
               throw new IllegalStateException("Column " + name + " at index " + position + " is greater than width " + width);
            }
            newCells[position] = cell;
         }
         Row mergedRow = mergeRowCells(schema, newCells, key, time, change);

         if (mergedRow != null) {
            int row = key.getIndex();
            Row previousRow = rows.get(row);           
            
            if(mergedRow != null) {
               rows.put(row, mergedRow);
            }
            return new DeltaMerge(schema, mergedRow, previousRow, key);
         }
      }
      return null;
   }

   private Row mergeRowCells(Schema schema, Cell[] newCells, Key key, long time, long change) {
      int row = key.getIndex();
      Row currentRow = rows.get(row);      
      Version update = version.update();
      String name = key.getName();

      if (currentRow != null) {         
         for (int i = 0; i < newCells.length; i++) { 
            if (newCells[i] == null) {
               newCells[i] = currentRow.getCell(i);
            }
         }
      }      
      return new Row(schema, update, newCells, name, time, change);
   }

   @Override
   public String toString() {
      return rows.toString();
   }
}
