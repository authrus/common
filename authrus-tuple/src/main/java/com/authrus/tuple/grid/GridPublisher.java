package com.authrus.tuple.grid;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class GridPublisher implements TuplePublisher {

   private final Catalog catalog;

   public GridPublisher(Catalog catalog) {
      this.catalog = catalog;
   }

   @Override
   public Tuple publish(Tuple tuple) {
      String type = tuple.getType();
      Grid grid = catalog.getGrid(type);
   
      if(grid == null) {
         throw new IllegalStateException("No grid found for '" + type + "'");
      }
      return grid.update(tuple);        
   }
}
