package com.authrus.tuple.subscribe;

import com.authrus.tuple.TupleListener;

public class SubscriptionService {
   
   private final SubscriptionConnection connection;
   private final TupleListener listener;

   public SubscriptionService(SubscriptionConnection connection, TupleListener listener) {
      this.connection = connection;
      this.listener = listener;
   }
   
   public void connect() {
      connection.register(listener);
      connection.connect();
   }
   
   public void close() {
      connection.remove(listener);
      connection.close();
   }
}
