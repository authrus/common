package com.authrus.io;

public enum DataFormat {
   INTEGER('I'), 
   TEXT('T'), 
   DOUBLE('D'), 
   FLOAT('F'), 
   LONG('L'), 
   SHORT('S'), 
   OCTET('O'), 
   BOOLEAN('B'), 
   CHARACTER('C');

   public final char code;

   private DataFormat(char code) {
      this.code = code;
   }

   public static DataFormat resolveFormat(char code) {
      if (code == 'I') {
         return INTEGER;
      }
      if (code == 'T') {
         return TEXT;
      }
      if (code == 'D') {
         return DOUBLE;
      }
      if (code == 'F') {
         return FLOAT;
      }
      if (code == 'L') {
         return LONG;
      }
      if (code == 'S') {
         return SHORT;
      }
      if (code == 'O') {
         return OCTET;
      }
      if (code == 'B') {
         return BOOLEAN;
      }
      if (code == 'C') {
         return CHARACTER;
      }    
      throw new IllegalArgumentException("No match for " + code);
   }

   public static DataFormat resolveFormat(Class type) {
      if (type == int.class) {
         return INTEGER;
      }
      if (type == double.class) {
         return DOUBLE;
      }
      if (type == float.class) {
         return FLOAT;
      }
      if (type == boolean.class) {
         return BOOLEAN;
      }
      if (type == byte.class) {
         return OCTET;
      }
      if (type == short.class) {
         return SHORT;
      }
      if (type == long.class) {
         return LONG;
      }
      if (type == char.class) {
         return CHARACTER;
      }
      if (type == String.class) {
         return TEXT;
      }
      if (type == Integer.class) {
         return INTEGER;
      }
      if (type == Double.class) {
         return DOUBLE;
      }
      if (type == Float.class) {
         return FLOAT;
      }
      if (type == Boolean.class) {
         return BOOLEAN;
      }
      if (type == Byte.class) {
         return OCTET;
      }
      if (type == Short.class) {
         return SHORT;
      }
      if (type == Long.class) {
         return LONG;
      }
      if (type == Character.class) {
         return CHARACTER;
      }
      throw new IllegalArgumentException("No match for " + type);
   }
}
