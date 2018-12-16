package com.authrus.tuple.grid;

import java.util.Arrays;

import com.authrus.predicate.Argument;

/**
 * A row represents a series of key value pairs within the grid. A row 
 * must have a key that uniquely identifies it. Typically a row will 
 * represent an object or record of some sort that is independent of 
 * other data.
 * 
 * @author Niall Gallagher
 */
public class Row implements Argument {

   private final Cell[] cells;
   private final Version version;
   private final Schema schema;
   private final String key;
   private final long change;   
   private final long time;

   public Row(Schema schema, Version version, Cell[] cells, String key, long time, long change) {
      this.version = version;
      this.schema = schema;
      this.change = change;
      this.time = time;
      this.cells = cells;
      this.key = key;
   }
   
   public long getTime() {
      return time;
   }

   public long getChange() {
      return change;
   }

   public int getCount() {
      return cells.length;
   }

   public String getKey() {
      return key;
   }   

   public Version getVersion() {
      return version;
   }

   public Cell[] getCells() {
      Cell[] copy = new Cell[cells.length];

      if (copy.length > 0) {
         System.arraycopy(cells, 0, copy, 0, cells.length);
      }
      return copy;
   }

   public Cell getCell(int column) {
      if (column < cells.length) {
         return cells[column];
      }
      return null;
   }

   public Cell getCell(String name) {
      Column column = schema.getColumn(name);

      if (column != null) {
         int index = column.getIndex();

         if (index < cells.length) {
            return cells[index];
         }
      }
      return null;
   }

   @Override
   public Object getValue(String name) {
      Cell cell = getCell(name);

      if (cell != null) {
         return cell.getValue();
      }
      return null;
   }

   @Override
   public String toString() {
      return Arrays.toString(cells);
   }
}
