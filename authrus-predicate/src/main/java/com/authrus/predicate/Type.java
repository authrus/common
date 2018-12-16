package com.authrus.predicate;

import java.math.BigDecimal;

public enum Type {
   ARGUMENT,   
   STRING,
   NUMBER,
   BOOLEAN;
   
   public boolean isArgument() {
      return this == ARGUMENT;
   }
   
   public boolean isBoolean() {
      return this == BOOLEAN;
   }
   
   public boolean isString() {
      return this == STRING;
   }
   
   public boolean isNumber() {
      return this == NUMBER;
   }
   
   public String format(String value) {
      if(this == STRING) {
         return String.format("'%s'", value);
      }
      return value;
   }

   public Comparable convert(String value) {
      if(this == NUMBER) {
         return new BigDecimal(value);
      }
      if(this == BOOLEAN) {
         return new Boolean(value);
      }
      return value;
   }
}
