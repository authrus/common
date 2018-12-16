package com.authrus.message.invoke;

import java.io.Closeable;
import java.io.IOException;

import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.subscribe.Subscription;

public class InvocationConnector {

   private final InvocationSubscriber connector;  
   private final OriginResolver resolver;

   public InvocationConnector(InvocationTracer tracer, InvocationDispatcher invoker, TransportBuilder builder, String source) throws Exception {
      this.resolver = new OriginResolver(source);
      this.connector = new InvocationSubscriber(tracer, invoker, builder);
   }

   public synchronized Closeable connect() throws IOException {
      Origin origin = resolver.resolveOrigin();

      if (origin == null) {
         throw new IOException("Could not resolve origin");
      }
      Subscription subscription = connector.subscribe(origin);
      
      if(subscription == null) {
         throw new IOException("Could not subscribe as " + origin);
      }
      return new SubscriptionHandle(subscription);
   }
   
   private class SubscriptionHandle implements Closeable {
      
      private final Subscription subscription;
      
      public SubscriptionHandle(Subscription subscription) {
         this.subscription = subscription;
      }
      
      public void close() {
         subscription.cancel();
      }
   }
}
