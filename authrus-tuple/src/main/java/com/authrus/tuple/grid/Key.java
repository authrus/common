package com.authrus.tuple.grid;

public class Key implements Index {

   private final Version version;
   private final String name;
   private final int row;
   private final long revision;

   public Key(String name, Version version, int row, long revision) {
      this.revision = revision;
      this.version = version;
      this.row = row;
      this.name = name;
   }
   
   public int getIndex() {
      return row;
   }
   
   public long getRevision() {
      return revision;
   }   

   public Version getVersion() {
      return version;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return String.format("%s@%s: %s", name, row, version);
   }
}
