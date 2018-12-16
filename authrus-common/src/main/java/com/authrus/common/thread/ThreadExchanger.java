package com.authrus.common.thread;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread exchanger is a type of thread pool that has no queuing. If 
 * offers a memory safe alternative to the traditional thread pooling 
 * where tasks are queued up before execution. Here the thread that is 
 * handing off the task will wait until the task is exchanged and 
 * executed begins execution before it returns.
 * 
 * @author Niall Gallagher
 */
public class ThreadExchanger implements Executor {

   private final BlockingQueue<Runnable> queue;
   private final ThreadPoolFactory factory;
   private final AtomicInteger active;
   private final int threads;
   private final long wait;

   public ThreadExchanger(ThreadPoolFactory factory) {
      this(factory, 10);
   }

   public ThreadExchanger(ThreadPoolFactory factory, int threads) {
      this(factory, threads, 60000);
   }

   public ThreadExchanger(ThreadPoolFactory factory, int threads, long wait) {
      this.queue = new SynchronousQueue(true);
      this.active = new AtomicInteger();
      this.factory = factory;
      this.threads = threads;
      this.wait = wait;
   }

   @Override
   public void execute(Runnable command) {
      int count = active.get();

      while (count < threads) {
         Worker worker = new Worker();

         if (active.compareAndSet(count, count + 1)) {
            Thread thread = factory.newThread(worker);

            if (thread != null) {
               thread.start();
            }
         }
         count = active.get();
      }
      try {
         queue.offer(command, wait, MILLISECONDS);
      } catch (Exception e) {
         command.run();
      }
   }

   private class Worker implements Runnable {

      @Override
      public void run() {
         try {
            while (true) {
               try {
                  Runnable command = queue.take();

                  if (command != null) {
                     command.run();
                  }
               } catch (InterruptedException e) {
                  throw new RuntimeException("Thread interrupted", e);
               }
            }
         } finally {
            active.getAndDecrement();
         }
      }

   }

}
