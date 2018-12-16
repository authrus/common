package com.authrus.tuple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleExchanger;
import com.authrus.tuple.TupleListener;

public class UpdateExchangerTest extends TestCase {

   private static final int ITERATIONS = 1000000;

   private static class Counter implements TupleListener {
      private final AtomicInteger updates = new AtomicInteger();
      private final AtomicInteger exceptions = new AtomicInteger();
      private final AtomicInteger resets = new AtomicInteger();
      private final AtomicInteger heartbeats = new AtomicInteger();

      @Override
      public void onUpdate(Tuple tuple) {
         updates.getAndIncrement();
      }

      @Override
      public void onException(Exception cause) {
         exceptions.getAndIncrement();
      }

      @Override
      public void onHeartbeat() {
         heartbeats.getAndIncrement();
      }

      @Override
      public void onReset() {
         resets.getAndIncrement();
      }

      public void clear() {
         resets.set(0);
         exceptions.set(0);
         heartbeats.set(0);
         updates.set(0);
      }

   }

   public void testThreadExchanger() throws Exception {
      Counter counter = new Counter();
      TupleExchanger exchanger = new TupleExchanger(counter, 10, 1000);
      Exception cause = new Exception();

      for (int x = 0; x < 2; x++) {
         long startTime = System.currentTimeMillis();
         for (int i = 0; i < ITERATIONS; i++) {
            exchanger.onHeartbeat();
         }
         System.err.println("Time taken for " + ITERATIONS + " onHeartbeat() was " + (System.currentTimeMillis() - startTime));
         Thread.sleep(1000);

         assertEquals(counter.heartbeats.get(), ITERATIONS);

         startTime = System.currentTimeMillis();
         for (int i = 0; i < ITERATIONS; i++) {
            exchanger.onReset();
         }
         System.err.println("Time taken for " + ITERATIONS + " onStale() was " + (System.currentTimeMillis() - startTime));
         Thread.sleep(1000);

         assertEquals(counter.heartbeats.get(), ITERATIONS);
         assertEquals(counter.resets.get(), ITERATIONS);

         startTime = System.currentTimeMillis();
         for (int i = 0; i < ITERATIONS; i++) {
            Map<String, Object> values = new HashMap<String, Object>();
            Tuple tuple = new Tuple(values, "X");
            
            exchanger.onUpdate(tuple);
         }
         System.err.println("Time taken for " + ITERATIONS + " onUpdate() was " + (System.currentTimeMillis() - startTime));
         Thread.sleep(1000);

         assertEquals(counter.updates.get(), ITERATIONS);
         assertEquals(counter.heartbeats.get(), ITERATIONS);
         assertEquals(counter.resets.get(), ITERATIONS);

         startTime = System.currentTimeMillis();
         for (int i = 0; i < ITERATIONS; i++) {
            exchanger.onException(cause);
         }
         System.err.println("Time taken for " + ITERATIONS + " onException() was " + (System.currentTimeMillis() - startTime));
         Thread.sleep(1000);

         assertEquals(counter.exceptions.get(), ITERATIONS);
         assertEquals(counter.updates.get(), ITERATIONS);
         assertEquals(counter.heartbeats.get(), ITERATIONS);
         assertEquals(counter.resets.get(), ITERATIONS);
         counter.clear();
      }

   }
}
