package com.authrus.tuple.queue;

import com.authrus.tuple.Tuple;

public class Queue {

   private final ElementConverter converter;
   private final InsertListener listener;
   private final String type;
   
   public Queue(InsertListener listener, String type) {
      this.converter = new ElementConverter(type);
      this.listener = listener;
      this.type = type;
   }

   public Tuple insert(Tuple tuple) {
      Element element = converter.toElement(tuple);

      if (listener != null) {
         ElementBatch batch = new ElementBatch();

         batch.insert(element);
         listener.onInsert(batch, type);
      }
      return tuple;
   }
}
