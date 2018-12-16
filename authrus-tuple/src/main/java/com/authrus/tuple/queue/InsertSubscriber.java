package com.authrus.tuple.queue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertSubscriber implements InsertListener {

   private static final Logger LOG = LoggerFactory.getLogger(InsertSubscriber.class);

   private final Map<String, InsertDispatcher> dispatchers;
   private final Set<String> addresses;
   private final Executor executor;

   public InsertSubscriber(Executor executor) {
      this(executor, 100000);
   }

   public InsertSubscriber(Executor executor, int capacity) {
      this.dispatchers = new ConcurrentHashMap<String, InsertDispatcher>();
      this.addresses = new CopyOnWriteArraySet<String>();
      this.executor = executor;
   }

   public void subscribe(String address, InsertListener listener) {
      InsertDispatcher dispatcher = new InsertDispatcher(listener, executor);

      dispatchers.put(address, dispatcher);
      addresses.add(address);
   }

   public void cancel(String address) {
      InsertDispatcher dispatcher = dispatchers.remove(address);

      if (dispatcher != null) {
         addresses.remove(address);
      }
   }

   @Override
   public void onInsert(ElementBatch batch, String type) {      
      for (String address : addresses) {
         InsertDispatcher dispatcher = dispatchers.get(address);

         try {
            if (dispatcher != null) {
               dispatcher.onInsert(batch, type);
            } else {
               addresses.remove(address);
            }
         } catch (Exception e) {
            LOG.info("Could not dispatch update to " + address + " of " + type, e);

            addresses.remove(address);
            dispatchers.remove(address);
         }
      }
   }
}
