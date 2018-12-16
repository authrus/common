package com.authrus.tuple.queue;

import java.util.Map;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class QueuePublisher implements TuplePublisher {

   private final Map<String, Queue> queues;

   public QueuePublisher(Map<String, Queue> queues) {
      this.queues = queues;
   }

   @Override
   public Tuple publish(Tuple tuple) {
      String type = tuple.getType();
      Queue queue = queues.get(type);

      if (queue == null) {
         throw new IllegalArgumentException("No queue for type '" + type + "'");
      }
      return queue.insert(tuple);
   }
}
