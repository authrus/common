package com.authrus.tuple.grid;

class RowMerger {

   private final Structure structure;
   private final Schema schema;

   public RowMerger(Structure structure, Schema schema) {
      this.structure = structure;
      this.schema = schema;
   }

   public Row merge(Row newRow, Row currentRow) {
      if (currentRow != null) {
         String key = currentRow.getKey();
         Version version = newRow.getVersion();
         Cell[] newCells = newRow.getCells();
         Cell[] currentCells = currentRow.getCells();
         Cell[] mergedCells = merge(newCells, currentCells, version);
         long change = currentRow.getChange();
         long update = newRow.getChange();
         long time = newRow.getTime();

         if(update == 0) { // not a replica
            return new Row(schema, version, mergedCells, key, time, change + 1);
         }
         if(update > change) {
            return new Row(schema, version, mergedCells, key, time, update);
         }
         return currentRow; // update is stale
      }
      return newRow;
   }

   private Cell[] merge(Cell[] newRow, Cell[] currentRow, Version version) {
      int cellCount = Math.max(newRow.length, currentRow.length);

      if (cellCount > 1) { // only merge for multiple cells
         return merge(newRow, currentRow, version, cellCount);
      }
      return newRow;
   }

   private Cell[] merge(Cell[] newRow, Cell[] currentRow, Version version, int cellCount) {
      Cell[] mergedRow = new Cell[cellCount]; // current row can never be larger than new row

      if (mergedRow.length > 0) {
         System.arraycopy(newRow, 0, mergedRow, 0, newRow.length);
      }
      String[] constants = structure.getConstants(); 
      
      for (int i = 0; i < currentRow.length; i++) {
         Cell currentCell = currentRow[i];
         Cell newCell = mergedRow[i];

         if (currentCell != null) { // it existed previously
            String name = currentCell.getColumn();
            Object currentValue = currentCell.getValue();

            if (newCell == null) {
               if(currentValue != null) {
                  mergedRow[i] = new Cell(name, null, version); // we must identify when a cell is removed
               } else {
                  mergedRow[i] = currentCell;
               }
            } else {
               Object newValue = newCell.getValue();

               if (currentValue != null) {
                  if (currentValue.equals(newValue)) {
                     mergedRow[i] = currentCell; // do not overwrite value if there was no change
                  }
               } else if(newValue == null) {
                  mergedRow[i] = currentCell; // null == null
               }
            }
         }
      }      
      for (String constant : constants) {
         Column column = schema.getColumn(constant);
         int index = column.getIndex();

         if(currentRow[index] != null) {
            if (mergedRow[index] != currentRow[index]) {
               throw new IllegalStateException("Constant '" + constant + "' has been modified");
            }
         }
      }
      return mergedRow;
   }
}
