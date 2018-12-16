package com.authrus.tuple.subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;

public class SubscriptionLogger implements SubscriptionListener {

   private static final Logger LOG = LoggerFactory.getLogger(SubscriptionLogger.class);

   @Override
   public void onConnect(String address) {
      LOG.info("Connection established for " + address);
   }

   @Override
   public void onClose(String address) {
      LOG.info("Connection closed for " + address);
   }

   @Override
   public void onException(String address, Exception cause) {
      LOG.info("Failure for " + address, cause);
   }

   @Override
   public void onSubscribe(String address, Query query) {
      LOG.info("Subscription for " + address + " updated to " + query);
   }   

   @Override
   public void onUpdate(String address, Tuple tuple) {
      LOG.info("Tuple for " + address + " was " + tuple);
   }   

   @Override
   public void onHeartbeat(String address) {
      LOG.info("Heartbeat for " + address);
   }
}
