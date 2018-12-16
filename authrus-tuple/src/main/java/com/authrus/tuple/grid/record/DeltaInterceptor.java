package com.authrus.tuple.grid.record;

import java.util.HashMap;
import java.util.Map;

import com.authrus.io.DataConsumer;
import com.authrus.io.DataReader;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.DeltaConsumer;

public class DeltaInterceptor implements DataConsumer {

   private final Map<String, DeltaConsumer> consumers;
   private final DeltaRecordListener listener;
   private final DeltaRecordBuilder builder;
   private final Session session;
   
   public DeltaInterceptor(DeltaRecordListener listener, Session session) {
      this.builder = new DeltaRecordBuilder(listener, session);
      this.consumers = new HashMap<String, DeltaConsumer>();
      this.listener = listener;
      this.session = session;
   }

   @Override
   public synchronized void consume(DataReader input) throws Exception {
      if(!input.readBoolean()) {
         throw new IllegalStateException("Delta type information missing");
      }
      String type = input.readString();
      DeltaConsumer consumer = consumers.get(type);
      
      if(consumer == null) {
         consumer = new DeltaConsumer(builder, type);
         consumers.put(type, consumer);
      }
      consumer.consume(input);
   }
   
   public synchronized void clear() {
      listener.onReset(session);
      consumers.clear();
   }
}
