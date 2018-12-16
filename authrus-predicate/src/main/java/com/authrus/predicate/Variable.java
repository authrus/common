package com.authrus.predicate;

public class Variable implements Predicate {
   
   private final String variable;
   
   public Variable(String variable) {
      this.variable = variable;
   }

   @Override
   public boolean accept(Argument accessor) {
      Object attribute = accessor.getValue(variable);
      String text = String.valueOf(attribute);

      return Boolean.valueOf(text);
   }

   @Override
   public String toString() {
      return String.format("(%s)", variable);
   }   
}
