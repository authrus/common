package com.authrus.store.tuple;

import java.nio.channels.SelectableChannel;

import org.simpleframework.transport.trace.Trace;
import org.simpleframework.transport.trace.TraceAnalyzer;

public class TupleStoreTracer implements TraceAnalyzer {
   
   @Override
   public Trace attach(SelectableChannel channel) {
      return new Trace() {

         @Override
         public void trace(Object event) {}

         @Override
         public void trace(Object event, Object value) {}
         
      };
   }

   @Override
   public void stop() {

   }

}
