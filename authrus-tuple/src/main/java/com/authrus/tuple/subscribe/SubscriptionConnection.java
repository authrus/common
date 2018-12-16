package com.authrus.tuple.subscribe;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleDistributor;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.TuplePublisher;
import com.authrus.tuple.query.Query;

public class SubscriptionConnection implements TuplePublisher {

   private final AtomicReference<Subscription> reference;
   private final Set<TupleListener> listeners;
   private final TupleDistributor distributor;
   private final Subscriber subscriber;
   private final Query query;
   private final boolean enable;

   public SubscriptionConnection(Subscriber subscriber, Query query) {
      this(subscriber, query, true);
   }

   public SubscriptionConnection(Subscriber subscriber, Query query, boolean enable) {
      this.listeners = new CopyOnWriteArraySet<TupleListener>();      
      this.reference = new AtomicReference<Subscription>();
      this.distributor = new TupleDistributor(listeners);
      this.subscriber = subscriber;
      this.enable = enable;
      this.query = query;
   }
   
   public void register(TupleListener listener) {
      if(enable) {
         listeners.add(listener);
      }
   }
   
   public void remove(TupleListener listener) {
      if(enable) {
         listeners.remove(listener);
      }
   }
   
   @Override
   public Tuple publish(Tuple tuple) {
      if(enable) {
         Subscription subscription = reference.get();
         
         if(subscription == null) {
            throw new IllegalStateException("Subscription for " + query + " not connected");
         }
         subscription.publish(tuple);  
         return tuple;
      }     
      return null;
   }   
   
   public void update(Query query) {
      if(enable) {
         Subscription subscription = reference.get();
         
         if(subscription == null) {
            throw new IllegalStateException("Subscription for " + query + " not connected");
         }
         subscription.update(query);   
      }
   }

   public void connect() {
      if(enable) {
         Subscription subscription = subscriber.subscribe(distributor, query);
   
         if (subscription == null) {
            throw new IllegalStateException("Subscription failed for " + query);
         }
         reference.set(subscription);         
      }
   }
   
   public void close() {
      if(enable) {
         Subscription subscription = reference.get();
         
         if(subscription != null) {
            subscription.cancel();
         }
      }
   }
}
