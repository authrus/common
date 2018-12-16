package com.authrus.message.invoke;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleAdapter;

public class ReturnValueListener extends TupleAdapter {

   private static final Logger LOG = LoggerFactory.getLogger(ReturnValueListener.class);

   private final InvocationBroker broker;
   private final InvocationBinder binder;

   public ReturnValueListener(InvocationBinder binder, InvocationBroker broker) {
      this.binder = binder;
      this.broker = broker;
   }

   @Override
   public void onUpdate(Tuple tuple) {
      try {
         Map<String, Object> attributes = tuple.getAttributes();
         ReturnValue response = (ReturnValue) binder.fromTuple(tuple);
         String operation = (String) attributes.get("operation");

         broker.notify(operation, response);
      } catch (Exception e) {
         LOG.info("Error occurred processing return value", e);
      }
   }

   @Override
   public void onException(Exception cause) {
      LOG.info("Error occurred", cause);
   }

   @Override
   public void onHeartbeat() {
      LOG.info("Heartbeat received");
   }
}
