package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import com.authrus.io.DataConsumer;
import com.authrus.io.DataReader;
import com.authrus.tuple.TupleListener;

public class DeltaDispatcher implements DataConsumer {
   
   private final Map<String, DeltaConsumer> consumers;
   private final DeltaMergeListener listener;
   
   public DeltaDispatcher(TupleListener listener) {
      this(listener, 30000);
   }
   
   public DeltaDispatcher(TupleListener listener, long expiry) {
      this.listener = new DeltaTupleListener(listener, expiry);
      this.consumers = new HashMap<String, DeltaConsumer>();      
   }

   @Override
   public synchronized void consume(DataReader reader) throws Exception {
      if(!reader.readBoolean()) {
         throw new IllegalStateException("Delta type information missing");
      }
      String type = reader.readString();
      DeltaConsumer consumer = consumers.get(type);
      
      try {
         if(consumer == null) {
            consumer = new DeltaConsumer(listener, type);
            consumers.put(type, consumer);
         }
         consumer.consume(reader);
      } catch(Exception e) {
         throw new IllegalStateException("Problem consuming delta for type " + type, e);
      }
   }
}
