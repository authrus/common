package com.authrus.message.invoke;

import static com.authrus.message.invoke.ReturnStatus.EXCEPTION;
import static com.authrus.message.invoke.ReturnStatus.TIMEOUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.CountDownLatch;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.tuple.query.Query;

public class InvocationBroker {

   private final Cache<String, CountDownLatch> cache;
   private final Cache<String, ReturnValue> results;
   private final Cache<String, Long> listeners;
   private final long timeout;

   public InvocationBroker() {
      this(20000);
   }

   public InvocationBroker(long timeout) {
      this(timeout, 10000);
   }

   public InvocationBroker(long timeout, int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, CountDownLatch>(capacity);
      this.results = new LeastRecentlyUsedCache<String, ReturnValue>(capacity);
      this.listeners = new LeastRecentlyUsedCache<String, Long>(capacity);
      this.timeout = timeout;
   }

   public void cancel(String address) {
      if (address != null) {
         listeners.take(address);
      }
   }

   public void subscribe(String address, Query query) {
      long time = System.currentTimeMillis();

      if (address != null) {
         listeners.cache(address, time);
      }
   }

   public void register(String operation, Invocation invocation) {
      CountDownLatch latch = new CountDownLatch(1);

      if (listeners.isEmpty()) {
         throw new IllegalStateException("Invocation has no subscribers");
      }
      if (operation == null) {
         throw new IllegalArgumentException("Registration requires a valid key");
      }
      cache.cache(operation, latch);
   }

   public ReturnValue wait(String operation) {
      try {
         CountDownLatch latch = cache.fetch(operation);

         try {
            latch.await(timeout, MILLISECONDS);
         } catch (Exception cause) {
            return new ReturnValue(EXCEPTION, cause);
         }
         ReturnValue value = results.take(operation);

         if (value == null) {
            return new ReturnValue(TIMEOUT, null);
         }
         return value;
      } finally {
         cache.take(operation);
      }
   }

   public void notify(String operation, ReturnValue value) {
      CountDownLatch latch = cache.fetch(operation);

      if (latch != null) {
         results.cache(operation, value);
         latch.countDown();
      }
   }
}
