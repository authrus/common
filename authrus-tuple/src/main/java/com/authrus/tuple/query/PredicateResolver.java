package com.authrus.tuple.query;

import java.util.Map;
import java.util.Set;

import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;
import com.authrus.predicate.PredicateParser;

public class PredicateResolver {

   private final Map<String, String> expressions;

   public PredicateResolver(Map<String, String> expressions) {
      this.expressions = expressions;
   }

   public Predicate resolve(String type) {
      Set<String> patterns = expressions.keySet();
      
      for(String pattern : patterns) {
         if(type.equals(pattern) || type.matches(pattern)) {
            String expression = expressions.get(pattern);
            
            if(!expression.equals("*")) {
               return new PredicateParser(expression);
            }
            return new Any();
         }
      }
      return null;
   }
}
