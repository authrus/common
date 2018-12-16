package com.authrus.tuple.grid;

/**
 * A cell represents a single key value pair within the grid. To ensure 
 * a cell is sent only when it changes it contains an immutable time 
 * stamp representing the creation time.
 * 
 * @author Niall Gallagher
 */
public class Cell {

   private final Version version;
   private final String column;
   private final Object value;

   public Cell(String column, Object value, Version version) {
      this.version = version;
      this.column = column;
      this.value = value;
   }

   public Version getVersion() {
      return version;
   }

   public String getColumn() {
      return column;
   }

   public Object getValue() {
      return value;
   }

   @Override
   public String toString() {
      return String.format("%s='%s' %s", column, value, version);
   }
}
