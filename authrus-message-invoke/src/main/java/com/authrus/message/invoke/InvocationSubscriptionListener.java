package com.authrus.message.invoke;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscription;
import com.authrus.tuple.subscribe.SubscriptionListener;

public class InvocationSubscriptionListener implements SubscriptionListener {

   private final Map<String, Subscription> subscriptions;
   private final ReturnValueListener listener;
   private final InvocationTracer tracer;
   private final InvocationBroker broker;

   public InvocationSubscriptionListener(InvocationBinder binder, InvocationTracer tracer, InvocationBroker broker, String owner) {
      this.subscriptions = new ConcurrentHashMap<String, Subscription>();
      this.listener = new ReturnValueListener(binder, broker);
      this.tracer = tracer;
      this.broker = broker;
   }   

   @Override
   public void onUpdate(String address, Tuple tuple) {
      listener.onUpdate(tuple); // return value from client
   }

   @Override
   public void onException(String address, Exception cause) {
      Subscription subscription = subscriptions.remove(address);

      if (subscription != null) {
         subscription.cancel();
      }
      broker.cancel(address);
      tracer.onException(address, cause);
   }

   @Override
   public void onSubscribe(String address, Query query) {
      broker.subscribe(address, query);
      tracer.onSubscribe(address, query);
   }

   @Override
   public void onConnect(String address) {
      broker.cancel(address);
      tracer.onConnect(address);
   }

   @Override
   public void onClose(String address) {
      tracer.onClose(address);
   }

   @Override
   public void onHeartbeat(String address) {
      tracer.onHeartbeat(address);
   }
}
