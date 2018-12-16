package com.authrus.tuple;

import java.util.Set;

import com.authrus.common.text.RegularExpressionFilter;

public class TupleFilter implements TuplePublisher {
   
   private volatile RegularExpressionFilter filter;
   private volatile TuplePublisher publisher;
   
   public TupleFilter(TuplePublisher publisher, Set<String> patterns) {
      this(publisher, patterns, false);
   }
   
   public TupleFilter(TuplePublisher publisher, Set<String> patterns, boolean allow) {
      this.filter = new RegularExpressionFilter(patterns, allow);
      this.publisher = publisher;
   }

   @Override
   public Tuple publish(Tuple tuple) {
      String type = tuple.getType();

      if(filter.accept(type)) {
         return publisher.publish(tuple);
      }
      return null;
   }
}
