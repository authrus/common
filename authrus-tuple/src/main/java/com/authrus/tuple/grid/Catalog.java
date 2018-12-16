package com.authrus.tuple.grid;

import java.util.Collections;
import java.util.Map;

public class Catalog {

   private final Map<String, Grid> grids;

   public Catalog() {
      this(Collections.EMPTY_MAP);
   }
   
   public Catalog(Map<String, Grid> grids) {
      this.grids = grids;
   }
   
   public Grid getGrid(String name) {
      return grids.get(name);
   }
   
   public Map<String, Grid> getGrids() {
      return grids;
   }
}
