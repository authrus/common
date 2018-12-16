package com.authrus.tuple.grid;

public class Structure {

   private final String[] constants;
   private final String[] key;
   
   public Structure(String[] key) {
      this(key, key);
   }
   
   public Structure(String[] key, String[] constants) {
      this.constants = constants;
      this.key = key;
   }
   
   public String[] getKey() {
      return key;
   }
   
   public String[] getConstants() {
      return constants;
   }
}
