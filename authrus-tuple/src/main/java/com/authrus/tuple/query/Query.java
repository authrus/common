package com.authrus.tuple.query;

import java.util.Collections;
import java.util.Map;

public class Query {

   private final Map<String, String> predicates;
   private final Origin origin;

   public Query(Origin origin) {
      this(origin, Collections.EMPTY_MAP);
   }

   public Query(Origin origin, Map<String, String> predicates) {
      this.predicates = predicates;
      this.origin = origin;
   }

   public Map<String, String> getPredicates() {
      return predicates;
   }

   public Origin getOrigin() {
      return origin;
   }

   @Override
   public String toString() {
      return String.format("%s: %s", origin, predicates);
   }
}
