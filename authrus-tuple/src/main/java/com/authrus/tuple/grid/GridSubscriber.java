package com.authrus.tuple.grid;

import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.FrameSubscription;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.Subscription;

public class GridSubscriber implements Subscriber {

   private final TransportBuilder builder;
   private final FrameTracer tracer;   
   private final long expiry;
   private final long retry;

   public GridSubscriber(FrameTracer tracer, TransportBuilder builder) {
      this(tracer, builder, 5000);
   }

   public GridSubscriber(FrameTracer tracer, TransportBuilder builder, long retry) {
      this(tracer, builder, retry, 30000);
   }
   
   public GridSubscriber(FrameTracer tracer, TransportBuilder builder, long retry, long expiry) {
      this.builder = builder;
      this.expiry = expiry;
      this.tracer = tracer;
      this.retry = retry;
   }

   @Override
   public Subscription subscribe(TupleListener listener, Query query) {
      try {
         FrameListener adapter = new GridListener(listener, expiry);
         FrameSubscription subscription = new FrameSubscription(adapter, tracer, builder, retry);
         
         subscription.subscribe(query);
         
         return subscription;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create subscription", e);
      }
   }
}
