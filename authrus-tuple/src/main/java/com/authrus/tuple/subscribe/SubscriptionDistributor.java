package com.authrus.tuple.subscribe;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;

public class SubscriptionDistributor implements SubscriptionListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(SubscriptionDistributor.class);

   private final List<SubscriptionListener> listeners;
   
   public SubscriptionDistributor(List<SubscriptionListener> listeners) {
      this.listeners = listeners;
   }
   
   @Override
   public void onUpdate(String address, Tuple tuple) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onUpdate(address, tuple);
         } catch(Exception e) {
            LOG.info("Error distributing message from " + address, e);
         }
      }
   }

   @Override
   public void onException(String address, Exception cause) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onException(address, cause);
         } catch(Exception e) {
            LOG.info("Error distributing exeception from " + address, e);
         }
      }
   }

   @Override
   public void onSubscribe(String address, Query query) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onSubscribe(address, query);
         } catch(Exception e) {
            LOG.info("Error distributing subscription from " + address, e);
         }
      }
   }

   @Override
   public void onHeartbeat(String address) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onHeartbeat(address);
         } catch(Exception e) {
            LOG.info("Error distributing heartbeat from " + address, e);
         }
      }
   }

   @Override
   public void onConnect(String address) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onConnect(address);
         } catch(Exception e) {
            LOG.info("Error distributing connect from " + address, e);
         }
      }
   }

   @Override
   public void onClose(String address) {
      for(SubscriptionListener listener : listeners) {
         try {
            listener.onClose(address);
         } catch(Exception e) {
            LOG.info("Error distributing close from " + address, e);
         }
      } 
   }

}
