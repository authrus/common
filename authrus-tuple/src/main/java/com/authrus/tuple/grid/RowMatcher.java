package com.authrus.tuple.grid;

import com.authrus.predicate.Argument;
import com.authrus.predicate.Predicate;

/**
 * The row matcher enforces the constraints applied to a grid. It will only
 * allow {@link Predicate} objects to reference constant cells for each
 * {@link Row}. If a predicate references a cell that is not a constant an
 * exception is thrown.
 * 
 * @author Niall Gallagher
 */
class RowMatcher {

   private final Structure structure;

   public RowMatcher(Structure structure) {
      this.structure = structure;
   }

   public boolean match(Predicate predicate, Row row) {
      return match(predicate, row, true);
   }
   
   public boolean match(Predicate predicate, Row row, boolean strict) {
      if(strict) {
         Argument argument = new ArgumentValidator(predicate, row);

         if (predicate == null) {
            throw new IllegalStateException("Predicate must not be null");
         }
         return predicate.accept(argument);
      }
      return predicate.accept(row);
   }

   private class ArgumentValidator implements Argument {

      private final Predicate predicate;
      private final Row row;

      private ArgumentValidator(Predicate predicate, Row row) {
         this.predicate = predicate;
         this.row = row;
      }

      @Override
      public Object getValue(String name) {
         String[] constants = structure.getConstants();
         
         for(int i = 0; i < constants.length; i++) {
            if (constants[i].equals(name)) {
               return row.getValue(name);
            }
         }
         throw new IllegalStateException("Attribute '" + name + "' referenced from '" + predicate + "' is not a constant");
      }
   }
}
