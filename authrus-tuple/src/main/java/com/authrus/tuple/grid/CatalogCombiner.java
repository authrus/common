package com.authrus.tuple.grid;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CatalogCombiner implements CatalogBuilder {

   private final Set<CatalogBuilder> builders;

   public CatalogCombiner(Set<CatalogBuilder> builders) {
      this.builders = builders;
   }

   @Override
   public Catalog createCatalog() {
      Map<String, Grid> map = new LinkedHashMap<String, Grid>();
      Catalog combination = new Catalog(map);
      
      for(CatalogBuilder builder : builders) {
         Catalog catalog = builder.createCatalog();
         Map<String, Grid> grids = catalog.getGrids();
         
         if(grids != null) {
            map.putAll(grids);
         }
      }
      return combination;
   }
}
