package com.authrus.tuple.grid;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;

public class CatalogQueryBuilder {

   private final Map<String, String> predicates;
   private final Catalog catalog;
   private final Query query;
   
   public CatalogQueryBuilder(Catalog catalog, Origin origin) {
      this.predicates = new LinkedHashMap<String, String>();
      this.query = new Query(origin, predicates);
      this.catalog = catalog;    
   }
   
   public synchronized Query createQuery() {
      Map<String, Grid> grids = catalog.getGrids();
      Set<String> types = grids.keySet();
      
      for(String type : types) {
         predicates.put(type, "*");
      }
      return query;
   }
}
