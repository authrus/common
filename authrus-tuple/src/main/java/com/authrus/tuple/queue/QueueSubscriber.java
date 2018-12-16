package com.authrus.tuple.queue;

import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.FrameSubscription;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.Subscription;

public class QueueSubscriber implements Subscriber {

   private final TransportBuilder builder;
   private final FrameTracer tracer;
   private final long retry;

   public QueueSubscriber(FrameTracer tracer, TransportBuilder builder) {
      this(tracer, builder, 5000);
   }

   public QueueSubscriber(FrameTracer tracer, TransportBuilder builder, long retry) {
      this.builder = builder;
      this.tracer = tracer;
      this.retry = retry;
   }

   @Override
   public Subscription subscribe(TupleListener listener, Query query) {
      try {
         FrameListener adapter = new QueueListener(listener);
         FrameSubscription subscription = new FrameSubscription(adapter, tracer, builder, retry);         
         
         subscription.subscribe(query);
         
         return subscription;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create subscription", e);
      }
   }
}
