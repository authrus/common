package com.authrus.common.memory;

public enum MemoryUnit {
   BYTE("Byte", "B", 1, 1),
   KILOBYTE("Kilobyte", "KB", 1024, 1),
   MEGABYTE("Megabyte", "MB", 1024, 2),
   GIGABYTE("Gigabyte", "GB", 1024, 3),
   TERABYTE("Terabyte", "TB", 1024, 4),
   PETABYTE("Petabyte", "PB", 1024, 5);
   
   public final String name;
   public final String code;
   public final double octets;
   
   private MemoryUnit(String name, String code, long octets, long power) {
      this.octets = Math.pow(octets, power);
      this.code = code;
      this.name = name;
   }
   
   public double toBytes(double value) {
      return convert(value, this, BYTE);
   }

   public double toKilobytes(double value) {
      return convert(value, this, KILOBYTE);
   }
   
   public double toMegabytes(double value) {
      return convert(value, this, MEGABYTE);
   }
   
   public double toGigabytes(double value) {
      return convert(value, this, GIGABYTE);
   }
   
   public double toTerabytes(double value) {
      return convert(value, this, TERABYTE);
   }
   
   public double convert(double value, MemoryUnit to) {
      return convert(value, this, to); 
   } 

   public static double convert(double value, MemoryUnit from, MemoryUnit to) {
      double factor = from.octets / to.octets;
      double scale = factor * value * 100.0;
      long result = Math.round(scale);
      
      return result / 100.0; // 2 decimal places 
   }  

   public static String format(double octets) {
      MemoryUnit[] units = values();      
      
      for(int i = 1; i < units.length; i++) {
         if(octets < units[i].octets) {
            return format(octets, units[i - 1]);
         }
      }
      return format(octets, units[units.length - 1]);
   }
   
   public static String format(double octets, MemoryUnit to) {
      return BYTE.convert(octets, to) + " " + to.code;
   }
}
