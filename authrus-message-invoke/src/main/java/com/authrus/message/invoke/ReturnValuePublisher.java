package com.authrus.message.invoke;

import java.util.Map;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.subscribe.Subscription;

public class ReturnValuePublisher {

   private final Subscription subscription;
   private final InvocationBinder binder;

   public ReturnValuePublisher(InvocationBinder binder, Subscription subscription) {
      this.subscription = subscription;
      this.binder = binder;
   }

   public void respond(ReturnValue value, String source, String operation) {  
      Tuple tuple = binder.toTuple(value);
      Map<String, Object> attributes = tuple.getAttributes();

      attributes.put("source", source);
      attributes.put("operation", operation);
      subscription.publish(tuple);
   }
}
