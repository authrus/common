package com.authrus.tuple;

import java.util.Collections;
import java.util.Set;

public class TupleDistributor implements TupleListener {
   
   private final Set<TupleListener> listeners;

   public TupleDistributor() {
      this(Collections.EMPTY_SET);
   }
   
   public TupleDistributor(Set<TupleListener> listeners) {
      this.listeners = listeners;
   }

   @Override
   public void onUpdate(Tuple tuple) {
      for(TupleListener listener : listeners) {
         listener.onUpdate(tuple);
      }
   }

   @Override
   public void onException(Exception cause) {
      for(TupleListener listener : listeners) {
         listener.onException(cause);
      }
   }

   @Override
   public void onHeartbeat() {
      for(TupleListener listener : listeners) {
         listener.onHeartbeat();
      }
   }

   @Override
   public void onReset() {
      for(TupleListener listener : listeners) {
         listener.onReset();
      }
   }      
} 
