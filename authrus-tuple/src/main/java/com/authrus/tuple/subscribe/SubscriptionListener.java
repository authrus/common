package com.authrus.tuple.subscribe;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;

public interface SubscriptionListener { 
   void onUpdate(String address, Tuple tuple); /* client message */
   void onException(String address, Exception cause);
   void onSubscribe(String address, Query query);
   void onHeartbeat(String address);
   void onConnect(String address);
   void onClose(String address);
}
