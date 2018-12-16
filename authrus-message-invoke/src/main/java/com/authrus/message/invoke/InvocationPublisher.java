package com.authrus.message.invoke;

import java.util.Map;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.queue.InsertListener;
import com.authrus.tuple.queue.Queue;

public class InvocationPublisher {

   private final InvocationBroker broker;
   private final InvocationBinder binder;
   private final Queue queue;
   private final String source;

   public InvocationPublisher(InvocationBinder binder, InvocationBroker broker, InsertListener listener, String source) {
      this.queue = new Queue(listener, "invoke");
      this.binder = binder;
      this.broker = broker;
      this.source = source;
   }

   public ReturnValue invoke(Invocation invocation, String operation) {
      Tuple tuple = binder.toTuple(invocation);
      Map<String, Object> attributes = tuple.getAttributes();
      
      attributes.put("source", source);
      attributes.put("operation", operation);
      broker.register(operation, invocation);
      queue.insert(tuple);

      return broker.wait(operation);
   }
}
