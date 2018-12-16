package com.authrus.tuple.queue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class InsertDispatcher implements InsertListener {

   private final AtomicReference<Exception> error;
   private final BlockingQueue<Insertion> queue;
   private final InsertNotifier notifier;
   private final AtomicInteger pending;
   private final Executor executor;
   private final int capacity;
   private final long wait;

   public InsertDispatcher(InsertListener listener, Executor executor) {
      this(listener, executor, 100000);
   }

   public InsertDispatcher(InsertListener listener, Executor executor, int capacity) {
      this(listener, executor, capacity, 5000);
   }

   public InsertDispatcher(InsertListener listener, Executor executor, int capacity, long wait) {
      this.queue = new ArrayBlockingQueue<Insertion>(capacity);
      this.error = new AtomicReference<Exception>();
      this.notifier = new InsertNotifier(listener);
      this.pending = new AtomicInteger();
      this.executor = executor;
      this.capacity = capacity;
      this.wait = wait;
   }

   @Override
   public void onInsert(ElementBatch batch, String type) {
      Exception cause = error.get();

      if(cause != null) {
         throw new IllegalStateException("Problem occured during update", cause);
      }
      if(notifier.isActive()) {
         Insertion insertion = new Insertion(batch, type);

         try {
            if(!queue.offer(insertion, wait, MILLISECONDS)) { // if full a thread is already running
               throw new IllegalStateException("Queue capacity " + capacity + " has been reached");
            }
            int count = pending.getAndIncrement();
            
            if(count == 0) { // do not fill the queue
               executor.execute(notifier);
            }            
         } catch(Exception e) {
            throw new IllegalStateException("Problem occurred dispatching update", e);
         }
      }
   }

   private class InsertNotifier implements Runnable {

      private final Map<String, ElementBatch> batches;
      private final InsertListener listener;
      private final AtomicBoolean active;
      private final Deque<Insertion> swap;
      private final Set<String> dirty;
      private final int limit;

      public InsertNotifier(InsertListener listener) {
         this(listener, 1000);
      }

      public InsertNotifier(InsertListener listener, int limit) {
         this.batches = new HashMap<String, ElementBatch>();
         this.dirty = new LinkedHashSet<String>();
         this.swap = new ArrayDeque<Insertion>();
         this.active = new AtomicBoolean(true);
         this.listener = listener;
         this.limit = limit;
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
                  sort();
                  update();
               }                
            }
         } catch (Exception cause) {
            active.set(false);
            error.set(cause);
         } finally {
            int remaining = pending.addAndGet(-count); // sets before the increment
            
            if(remaining > 0) { 
               executor.execute(this); // get to back of queue
            }
         } 
      }

      private void sort() {
         int size = swap.size();
         int count = Math.min(limit, size); // limit batch size

         for(int i = 0; i < count; i++) {
            Insertion insertion = swap.poll();

            if(insertion != null) {
               String type = insertion.getType();
               ElementBatch update = insertion.getBatch();
               ElementBatch batch = batches.get(type);

               if(batch == null) {
                  batch = new ElementBatch();
                  batches.put(type, batch);
               }
               batch.insert(update);
               dirty.add(type);
            }
         }
      }

      private void update() {
         int size = dirty.size();
         
         if(size > 0) {
            for(String type : dirty) {
               ElementBatch batch = batches.get(type);
   
               if(!batch.isEmpty()) {               
                  listener.onInsert(batch, type);
               }
               batch.clear();
            }
            dirty.clear();
         }
      }
   }

   private class Insertion {

      private final ElementBatch batch;
      private final String type;

      public Insertion(ElementBatch batch, String type) {
         this.batch = batch;
         this.type = type;
      }

      public ElementBatch getBatch() {
         return batch;
      }

      public String getType() {
         return type;
      }
   }
}
