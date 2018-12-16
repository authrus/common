package com.authrus.predicate;

public class Not implements Predicate {

   private final Predicate predicate;

   public Not(Predicate predicate) {
      this.predicate = predicate;
   }

   @Override
   public boolean accept(Argument accessor) {
      return !predicate.accept(accessor);
   }

   @Override
   public String toString() {
      return String.format("!(%s)", predicate);
   }
}
