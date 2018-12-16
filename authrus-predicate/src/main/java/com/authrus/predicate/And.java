package com.authrus.predicate;

public class And implements Predicate {

   private final Predicate left;
   private final Predicate right;

   public And(Predicate left, Predicate right) {
      this.left = left;
      this.right = right;
   }

   @Override
   public boolean accept(Argument accessor) {
      return left.accept(accessor) && right.accept(accessor);
   }

   @Override
   public String toString() {
      return String.format("(%s && %s)", left, right);
   }
}
