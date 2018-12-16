package com.authrus.tuple.query;

import java.util.Map;
import java.util.Set;

import com.authrus.predicate.PredicateParser;

public class QueryValidator {
   
   private final PredicateParser parser;

   public QueryValidator() {
      this.parser = new PredicateParser();
   }
   
   public synchronized void validate(Query query) {
      Map<String, String> predicates = query.getPredicates();
      Set<String> types = predicates.keySet();
      
      for(String type : types) {
         String expression = predicates.get(type);
         
         try {                  
            parser.parse(expression);
         } catch(Exception e) {
            throw new IllegalStateException("Invalid expression '" + expression + "' for '" + type + "'", e);
         }
      }
   }
}
