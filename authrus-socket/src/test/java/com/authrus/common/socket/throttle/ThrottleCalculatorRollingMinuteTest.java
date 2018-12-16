package com.authrus.common.socket.throttle;

import junit.framework.TestCase;

public class ThrottleCalculatorRollingMinuteTest extends TestCase {

   public static class MockClock extends ThrottleClock {

      private long time;

      public void addTime(long time) {
         this.time += time;
      }

      public void setTime(long time) {
         this.time = time;
      }

      public long currentTime() {
         return time;
      }
   }

   public void testRollingMinuteThrottleDelay() {
      MockClock clock = new MockClock();
      ThrottleCapacity throttleCapacity = new ThrottleCapacity(60000, 50);
      ThrottleCalculator calculator = new ThrottleCalculator(throttleCapacity, clock);
      long totalSent = 0;

      for (int i = 0; i < 1000; i++) {
         ThrottleResult result = calculator.update(200);
         long calculateDelay = result.getThrottleDelay();
         long currentTime = clock.currentTime();
         totalSent += 100;

         System.err.println("Delay applied is " + calculateDelay + " after " + i + " iterations and " + currentTime + " milliseconds, total sent is " + totalSent);
         clock.addTime(100);
      }
   }

   public void testRollingCapacity() {
      MockClock clock = new MockClock();
      ThrottleCapacity throttleCapacity = new ThrottleCapacity(60000, 50);
      ThrottleCalculator calculator = new ThrottleCalculator(throttleCapacity, clock);

      for (int i = 0; i < 1000; i++) {
         long capacityUsed = calculator.updateUsedCapacity(100);
         long currentTime = clock.currentTime();

         System.err.println("Capacity used at iteration " + i + " is " + capacityUsed + " at time " + currentTime);
         clock.addTime(100);
      }
      assertEquals(calculator.updateUsedCapacity(100), 600 * 100);
   }

   public void testThrottle() {
      MockClock clock = new MockClock();
      ThrottleCapacity throttleCapacity = new ThrottleCapacity(60000, 50);
      ThrottleCalculator calculator = new ThrottleCalculator(throttleCapacity, clock);

      assertEquals(calculator.updateUsedCapacity(100), 100);
      assertEquals(calculator.updateUsedCapacity(100), 200);
      assertEquals(calculator.updateUsedCapacity(100), 300);
      assertEquals(calculator.updateUsedCapacity(100), 400);
      assertEquals(calculator.updateUsedCapacity(100), 500);
   }

}
