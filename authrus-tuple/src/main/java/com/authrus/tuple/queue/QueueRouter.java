package com.authrus.tuple.queue;

import java.util.Map;

import com.authrus.common.collections.LeastRecentlyUsedMap;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class QueueRouter implements TuplePublisher {

   private volatile Map<String, Queue> queues;
   private volatile QueueAllocator allocator;
   
   public QueueRouter(InsertListener listener) {
      this(listener, 1000);
   }
   
   public QueueRouter(InsertListener listener, int capacity) {
      this.queues = new LeastRecentlyUsedMap<String, Queue>(capacity);
      this.allocator = new QueueAllocator(listener, capacity);      
   }

   @Override
   public Tuple publish(Tuple tuple) {
      String type = tuple.getType();
      Queue queue = queues.get(type);

      if (queue == null) {
         queue = allocator.allocate(type);        
      }
      return queue.insert(tuple);
   }
   
   private class QueueAllocator {
      
      private final InsertListener listener;
      private final int capacity;
      
      public QueueAllocator(InsertListener listener, int capacity) {
         this.listener = listener;
         this.capacity = capacity;
      }

      public synchronized Queue allocate(String type) {
         Queue existing = queues.get(type);
         
         if(existing == null) {
            Map<String, Queue> update = new LeastRecentlyUsedMap<String, Queue>(capacity);
            Queue queue = new Queue(listener, type);
         
            update.putAll(queues);
            update.put(type, queue);
            queues = update;
            
            return queue;
         }
         return existing;
      }
   }
}
