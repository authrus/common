package com.authrus.tuple.grid;

public class Column implements Index {

   private final Version version;
   private final String name;
   private final int column;

   public Column(String name, Version version, int column) {
      this.version = version;
      this.column = column;
      this.name = name;
   }

   public Version getVersion() {
      return version;
   }

   public String getName() {
      return name;
   }

   public int getIndex() {
      return column;
   }

   @Override
   public String toString() {
      return String.format("%s:%s", name, column);
   }
}
