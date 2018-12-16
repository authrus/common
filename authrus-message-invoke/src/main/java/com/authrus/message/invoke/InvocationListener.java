package com.authrus.message.invoke;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleAdapter;

public class InvocationListener extends TupleAdapter {

   private static final Logger LOG = LoggerFactory.getLogger(InvocationListener.class);

   private final InvocationDispatcher dispatcher;
   private final ReturnValuePublisher responder;
   private final InvocationTracer tracer;
   private final InvocationBinder binder;

   public InvocationListener(InvocationBinder binder, InvocationTracer tracer, InvocationDispatcher dispatcher, ReturnValuePublisher responder) {
      this.dispatcher = dispatcher;
      this.responder = responder;
      this.binder = binder;
      this.tracer = tracer;
   }

   @Override
   public void onUpdate(Tuple tuple) {      
      try {
         Map<String, Object> attributes = tuple.getAttributes();
         Invocation invocation = (Invocation) binder.fromTuple(tuple);       
         String operation = (String) attributes.get("operation");
         String source = (String) attributes.get("source");

         tracer.onInvoke(operation, invocation);

         try {
            ReturnValue value = dispatcher.dispatch(invocation);

            if (responder != null) {
               responder.respond(value, source, operation);
            }
         } finally {
            tracer.onReturn(operation);
         }
      } catch (Exception cause) {
         LOG.info("Error processing message", cause);

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
