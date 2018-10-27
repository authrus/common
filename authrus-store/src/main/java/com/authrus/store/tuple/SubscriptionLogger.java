package com.authrus.store.tuple;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.zuooh.tuple.Tuple;
import com.zuooh.tuple.query.Query;
import com.zuooh.tuple.subscribe.SubscriptionAdapter;

@Slf4j
@Component
public class SubscriptionLogger extends SubscriptionAdapter {

   @Override
   public void onConnect(String address) {
      log.debug("Connection established for " + address);
   }

   @Override
   public void onClose(String address) {
      log.debug("Connection closed for " + address);
   }

   @Override
   public void onException(String address, Exception cause) {
      log.debug("Failure for " + address, cause);
   }

   @Override
   public void onSubscribe(String address, Query query) {
      log.debug("Subscription for " + address + " updated to " + query);
   }   

   @Override
   public void onUpdate(String address, Tuple tuple) {
      log.debug("Tuple for " + address + " was " + tuple);
   }   

   @Override
   public void onHeartbeat(String address) {
      log.debug("Heartbeat for " + address);
   }
}
