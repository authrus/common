package com.authrus.predicate;

public class Any implements Predicate {

   @Override
   public boolean accept(Argument accessor) {
      return true;
   }

   @Override
   public String toString() {
      return "(*)";
   }
}
