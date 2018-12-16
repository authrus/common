package com.authrus.message.invoke;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.TupleDistributor;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.FrameSubscription;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.queue.QueueListener;
import com.authrus.tuple.subscribe.Subscription;

public class InvocationSubscriber {

   private final InvocationDispatcher invoker;
   private final InvocationTracer tracer;
   private final InvocationBinder binder;
   private final TransportBuilder builder;
   private final FrameAdapter adapter;

   public InvocationSubscriber(InvocationTracer tracer, InvocationDispatcher invoker, TransportBuilder builder) {
      this.binder = new InvocationBinder();
      this.adapter = new FrameAdapter();
      this.builder = builder;
      this.invoker = invoker;
      this.tracer = tracer;
   }

   public Subscription subscribe(Origin origin) throws IOException {  
      Map<String, String> predicates = new HashMap<String, String>();
      Query query = new Query(origin, predicates);
      Set<TupleListener> listeners = new HashSet<TupleListener>();
      TupleDistributor distributor = new TupleDistributor(listeners);
      FrameListener queue = new QueueListener(distributor);
      FrameSubscription subscription = new FrameSubscription(queue, adapter, builder, 5000);         
      ReturnValuePublisher publisher = new ReturnValuePublisher(binder, subscription);
      InvocationListener listener = new InvocationListener(binder, tracer, invoker, publisher); 

      predicates.put("invoke", "*"); // listen to all invocations
      listeners.add(listener);
      subscription.subscribe(query);
      
      return subscription;
   }    
}
