package com.authrus.common.time;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class SystemClockTest extends TestCase {

   public void testMultiThreadTime() throws Exception {
      final SystemClock clock = new SystemClock();
      final TimeBarrier barrier = new TimeBarrier(clock);
      final List<Thread> threads = new ArrayList<Thread>();

      for (int i = 0; i < 10; i++) {
         Thread thread = new Thread(new Runnable() {
            public void run() {
               for (int i = 0; i < 100000; i++) {
                  Time time = clock.currentTime();
                  Time created = barrier.createTime();
                  boolean lessThanOrEqual = time.before(created) || time.sameTime(created);

                  if (!lessThanOrEqual) {
                     assertTrue("Time should be less than created time", lessThanOrEqual);
                  }
               }
            }
         });
         threads.add(thread);
      }
      for (Thread thread : threads) {
         thread.start();
      }
      for (Thread thread : threads) {
         thread.join();
      }
   }

   public void testTimeIncrements() throws Exception {
      SystemClock clock = new SystemClock();
      Time previous = null;

      for (int i = 0; i < 10000000; i++) {
         Time time = clock.currentTime();

         if (previous != null) {
            boolean greaterOrEqual = time.after(previous) || time.sameTime(previous);

            if (!greaterOrEqual) {
               assertTrue("Time did not increment", greaterOrEqual);
            }
         }
         previous = time;
      }
   }

   public static class TimeBarrier {

      private final Clock clock;

      public TimeBarrier(Clock clock) {
         this.clock = clock;
      }

      public synchronized Time createTime() {
         return clock.currentTime();
      }
   }
}
