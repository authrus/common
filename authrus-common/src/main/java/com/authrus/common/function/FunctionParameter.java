package com.authrus.common.function;

public class FunctionParameter {

   private final String name;
   private final String value;
   private final boolean quote;

   public FunctionParameter(String name, String value) {
      this(name, value, false);
   }

   public FunctionParameter(String name, String value, boolean quote) {
      this.quote = quote;
      this.name = name;
      this.value = value;
   }

   public boolean isQuote() {
      return quote;
   }

   public String getName() {
      return name;
   }

   public String getValue() {
      return value;
   }

   @Override
   public String toString() {
      return String.format("%s: %s", name, value);
   }
}
