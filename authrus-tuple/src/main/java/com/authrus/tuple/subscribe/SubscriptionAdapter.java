package com.authrus.tuple.subscribe;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;

public class SubscriptionAdapter implements SubscriptionListener {
   public void onUpdate(String address, Tuple tuple) {}
   public void onException(String address, Exception cause) {}
   public void onSubscribe(String address, Query query) {}
   public void onHeartbeat(String address) {}
   public void onConnect(String address) {}
   public void onClose(String address) {}
}
