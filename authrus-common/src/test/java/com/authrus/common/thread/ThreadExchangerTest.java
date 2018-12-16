package com.authrus.common.thread;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class ThreadExchangerTest extends TestCase {

   private static final int ITERATIONS = 1000000;

   public static class Counter implements Runnable {
      public final AtomicInteger counter = new AtomicInteger();

      @Override
      public void run() {
         counter.getAndIncrement();
      }
   }

   public void testThreadExchanger() throws Exception {
      ThreadPoolFactory factory = new ThreadPoolFactory(Counter.class);
      ThreadExchanger executor = new ThreadExchanger(factory, 10, 3000);
      Counter counter = new Counter();

      for (int i = 0; i < ITERATIONS; i++) {
         executor.execute(counter);
      }
      Thread.sleep(1000);
      assertEquals(counter.counter.get(), ITERATIONS);
   }
}
