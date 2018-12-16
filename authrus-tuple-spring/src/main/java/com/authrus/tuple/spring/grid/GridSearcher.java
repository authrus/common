package com.authrus.tuple.spring.grid;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.html.TableDrawer;
import com.authrus.common.html.TableRowDrawer;
import com.authrus.predicate.Predicate;
import com.authrus.predicate.PredicateParser;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.Grid;

@ManagedResource(description="Search through grid messages")
public class GridSearcher {

   private final Catalog catalog;
   private final int limit;
   
   public GridSearcher(Catalog catalog) {
      this(catalog, 2000000);
   } 
   
   public GridSearcher(Catalog catalog, int limit) {
      this.catalog = catalog;   
      this.limit = limit;   
   }
   
   @ManagedOperation(description="Search for tuples by predicate")
   @ManagedOperationParameters({     
      @ManagedOperationParameter(name="expression", description="Predicate expression to use")
   })   
   public String searchByPredicate(String expression) {
      Map<String, Grid> grids = catalog.getGrids();
      Set<String> types = grids.keySet();
      
      if(!types.isEmpty()) {
         Predicate predicate = new PredicateParser(expression);
         StringBuilder builder = new StringBuilder();
         
         for(String type : types) {
            Grid grid = grids.get(type);
            List<Tuple> tuples = grid.find(predicate);
            
            for(Tuple message : tuples) {
               int length = builder.length();
               
               if(length < limit) {
                  String description = searchDescription(message);
                  String result = searchResult(message);
                  
                  builder.append(description);
                  builder.append(result);
                  builder.append("<br>");   
                  builder.append("<hr>");
               }
            }
         }
         return builder.toString();
      }
      return null;
   }
   
   @ManagedOperation(description="Search for tuples by key")
   @ManagedOperationParameters({   
      @ManagedOperationParameter(name="term", description="Key to find record with")
   })   
   public String searchByKey(String term) {
      Map<String, Grid> grids = catalog.getGrids();
      Set<String> types = grids.keySet();
      
      if(!types.isEmpty()) {
         StringBuilder builder = new StringBuilder();
            
         for(String type : types) {
            Grid grid = grids.get(type);
            Tuple tuple = grid.find(term);
            
            if(tuple != null) {
               String description = searchDescription(tuple);
               String result = searchResult(tuple);
               
               builder.append(description);
               builder.append(result);
               builder.append("<hr>");
               builder.append("<br>");
            }
         }         
         return builder.toString();
      }
      return null;      
   }  

   private String searchDescription(Tuple tuple) {
      StringBuilder builder = new StringBuilder();
      
      if(tuple != null) {
         String type = tuple.getType();
         
         builder.append("<h2>");
         builder.append(type);
         builder.append("</h2>");
      }
      return builder.toString();      
   }
   
   private String searchResult(Tuple tuple) {
      TableDrawer table = new TableDrawer("attribute", "value", "format");
      SortedSet<String> order = new TreeSet<String>();

      if(tuple != null) {
         Map<String, Object> attributes = tuple.getAttributes();
         Set<String> keys = attributes.keySet();
         
         for(String key : keys) {
            order.add(key);
         }
         for(String key : order) {
            TableRowDrawer row = table.newRow(); 
            Object value = attributes.get(key);
            Class type = value.getClass();
            String format = type.getName();

            row.setBold("attribute", key);
            row.setNormal("value", value);
            row.setNormal("format", format);
         }  
      }
      return table.drawTable();
   }   
}
