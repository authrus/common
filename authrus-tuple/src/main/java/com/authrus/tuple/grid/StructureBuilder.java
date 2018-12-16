package com.authrus.tuple.grid;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StructureBuilder implements CatalogBuilder {
   
   private final Map<String, Structure> structures;
   private final Map<String, Grid> grids;
   private final ChangeListener listener;
   private final Catalog catalog;

   public StructureBuilder(Map<String, Structure> structures, ChangeListener listener) {
      this.grids = new LinkedHashMap<String, Grid>();
      this.catalog = new Catalog(grids);
      this.structures = structures;
      this.listener = listener;    
   }
   
   @Override
   public synchronized Catalog createCatalog() {
      if(grids.isEmpty()) {         
         Set<String> types = structures.keySet();
         
         for(String type : types) {
            Structure structure = structures.get(type);
            
            if(structure != null) {
               Grid grid = new Grid(listener, structure, type);
               
               if(grid.contains(type)) {
                  throw new IllegalStateException("Type '" + type + "' has already been definied");
               }
               grids.put(type, grid);
            }
         }
      }
      return catalog;
   }
}
