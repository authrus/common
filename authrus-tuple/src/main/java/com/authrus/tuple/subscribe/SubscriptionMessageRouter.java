package com.authrus.tuple.subscribe;

import java.util.Collections;
import java.util.Set;

import com.authrus.common.text.RegularExpressionFilter;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class SubscriptionMessageRouter extends SubscriptionAdapter {
   
   private final RegularExpressionFilter filter;
   private final TuplePublisher publisher;
   
   public SubscriptionMessageRouter(TuplePublisher publisher) {
      this(publisher, Collections.EMPTY_SET);
   }
   
   public SubscriptionMessageRouter(TuplePublisher publisher, Set<String> patterns) {
      this(publisher, patterns, false);      
   }
   
   public SubscriptionMessageRouter(TuplePublisher publisher, Set<String> patterns, boolean allow) {
      this.filter = new RegularExpressionFilter(patterns, allow);     
      this.publisher = publisher;
   }

   @Override
   public void onUpdate(String address, Tuple tuple) {
      if(filter.accept(address)) {
         publisher.publish(tuple);
      }
   }
}
