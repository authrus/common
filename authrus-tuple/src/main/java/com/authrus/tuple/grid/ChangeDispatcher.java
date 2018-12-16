package com.authrus.tuple.grid;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class ChangeDispatcher implements ChangeListener {

   private final ConcurrentMap<String, Change> changes;
   private final AtomicReference<Exception> error;
   private final BlockingQueue<String> queue;   
   private final ChangeNotifier notifier;
   private final AtomicInteger pending;
   private final Executor executor;

   public ChangeDispatcher(ChangeListener listener, Executor executor) {
      this.changes = new ConcurrentHashMap<String, Change>();
      this.queue = new LinkedBlockingQueue<String>();
      this.error = new AtomicReference<Exception>();
      this.notifier = new ChangeNotifier(listener);
      this.pending = new AtomicInteger();
      this.executor = executor;
   }

   @Override
   public void onChange(Grid grid, Schema schema, String type) {
      Exception cause = error.get();
      
      if(cause != null) {
         throw new IllegalStateException("Problem processing change", cause);
      } 
      if(notifier.isActive()) {
         Change change = new Change(grid, schema);
   
         if (changes.put(type, change) == null) {
            if(queue.offer(type)) {            
               int count = pending.getAndIncrement();
               
               if(count == 0) { // do not fill the queue
                  executor.execute(notifier);
               }  
            }
         }      
      }
   }

   private class ChangeNotifier implements Runnable {

      private final ChangeListener listener;
      private final Deque<String> swap;
      private final AtomicBoolean active;

      public ChangeNotifier(ChangeListener listener) {
         this.swap = new ArrayDeque<String>();
         this.active = new AtomicBoolean(true);
         this.listener = listener;
      }
      
      public boolean isActive() {
         return active.get();
      }

      @Override
      public void run() {
         int count = 0;
         
         try {
            while(active.get()) {
               int require = pending.get();
               
               if(count == require) {
                  break;
               }
               count += queue.drainTo(swap);

               while(!swap.isEmpty()) {
                  String type = swap.poll();
                  Change change = changes.remove(type);
                  Grid grid = change.getGrid();
                  Schema schema = change.getSchema();
   
                  listener.onChange(grid, schema, type);                
               }
            }
         } catch (Exception cause) {
            active.set(false);
            error.set(cause);
         } finally {
            int remaining = pending.addAndGet(-count); // sets before the increment 
            
            if(remaining > 0) { 
               executor.execute(this); // get to the back of the queue 
            }
         }
      }
   }

   private class Change {

      private final Schema schema;
      private final Grid grid;

      public Change(Grid grid, Schema schema) {
         this.schema = schema;
         this.grid = grid;
      }

      public Grid getGrid() {
         return grid;
      }

      public Schema getSchema() {
         return schema;
      }
   }
}

