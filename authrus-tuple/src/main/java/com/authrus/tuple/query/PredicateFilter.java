package com.authrus.tuple.query;

import java.util.HashMap;
import java.util.Map;

import com.authrus.predicate.Predicate;

public class PredicateFilter {

   private final Map<String, Predicate> predicates;
   private final PredicateResolver resolver;

   public PredicateFilter(Map<String, String> expressions) {
      this.resolver = new PredicateResolver(expressions);
      this.predicates = new HashMap<String, Predicate>(); 
   }

   public Predicate getPredicate(String type) {
      Predicate predicate = predicates.get(type);
      
      if(predicate == null) {
         if(!predicates.containsKey(type)) {
            predicate = resolver.resolve(type);
            predicates.put(type, predicate);
         }
      }
      return predicate;
   }
}
