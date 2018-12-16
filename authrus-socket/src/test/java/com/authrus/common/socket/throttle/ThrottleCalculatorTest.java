package com.authrus.common.socket.throttle;

import java.text.DecimalFormat;

import junit.framework.TestCase;

public class ThrottleCalculatorTest extends TestCase {

   private static boolean RUN_THIS_TEST = false;

   public void testMinutesWithSomeFreeCapacityAndLargePackets() throws Exception {
      processThrottleWith("MINUTES-LARGE-FREE", 20000, 600000, 70, 1000);
   }

   public void testMinutesWithSomeFreeCapacityAndMediumPackets() throws Exception {
      processThrottleWith("MINUTES-MEDIUM-FREE", 20000, 600000, 50, 200); // Check
                                                                          // this
   }

   public void testMinutesWithSomeFreeCapacityAndSmallPackets() throws Exception {
      processThrottleWith("MINUTES-SMALL-FREE", 20000, 600000, 50, 10);
   }

   public void testMinutesWithNoFreeCapacityAndLargePackets() throws Exception {
      processThrottleWith("MINUTES-LARGE", 20000, 100000, 0, 1000);
   }

   public void testMinutesWithNoFreeCapacityAndMediumPackets() throws Exception {
      processThrottleWith("MINUTES-MEDIUM", 20000, 100000, 0, 200);
   }

   public void testMinutesWithNoFreeCapacityAndSmallPackets() throws Exception {
      processThrottleWith("MINUTES-SMALL", 20000, 100000, 0, 10);
   }

   public void processThrottleWith(String tag, long durationOfTestInMillis, long totalBytes, int freePercent, int packetSize) throws Exception {
      if (RUN_THIS_TEST) {
         if (freePercent > 100) {
            throw new IllegalStateException("Free percent must be less than 100%");
         }
         ThrottleCapacity capacity = new ThrottleCapacity(totalBytes, freePercent);
         ThrottleClock clock = new ThrottleClock();
         ThrottleCalculator calculator = new ThrottleCalculator(capacity, clock); // 10
                                                                                  // slots
                                                                                  // =
                                                                                  // tenth
                                                                                  // of
                                                                                  // a
                                                                                  // second
                                                                                  // per
                                                                                  // slot
                                                                                  // =
                                                                                  // 100
                                                                                  // milliseconds
         DecimalFormat format = new DecimalFormat("####.######");
         DecimalFormat percentFormat = new DecimalFormat("###.##");
         double maximumBytesSecond = totalBytes * (freePercent * 0.01);
         double overallBytesSecond = totalBytes / 60;
         long startTime = System.currentTimeMillis();
         double totalSent = 0;
         double totalDelay = 0;

         for (int i = 0; true; i++) {
            ThrottleResult result = calculator.update(packetSize);
            String freePercentUsed = percentFormat.format(result.getFreePercentageUsed());
            String totalPercentUsed = percentFormat.format(result.getTotalPercentageUsed());
            long delay = result.getThrottleDelay();

            // if(delay > 0) {
            // System.err.printf("[%s] iteration=%s delay=%s freePercentUsed=%s%s totalPecentUsed=%s%s%n",
            // tag, i, delay, freePercentUsed, "%", totalPercentUsed, "%");
            // }

            Thread.sleep(delay);

            double timeElapsed = System.currentTimeMillis() - startTime;
            long secondsElapsed = Math.round(timeElapsed / 1000.0);

            totalDelay += delay;
            totalSent += packetSize;

            // if(delay > 0) {
            String totalSecondsElapsed = format.format(secondsElapsed);
            String overallBytesPerSecond = format.format(overallBytesSecond);
            String maximumBytesPerSecond = format.format(maximumBytesSecond);
            String actualBytesPerSecond = format.format(totalSent);

            if (secondsElapsed > 0) {
               actualBytesPerSecond = format.format(totalSent / secondsElapsed);
            }

            System.err.printf("[%s] iteration=%s delay=%s seconds=%s max=%s/s, overall=%s/s actual=%s/s freeUsed=%s%s totalUsed=%s%s totalBytes=%s, totalSent=%s totalDelay=%s%n", tag, i, delay,
                  totalSecondsElapsed, maximumBytesPerSecond, overallBytesPerSecond, actualBytesPerSecond, freePercentUsed, "%", totalPercentUsed, "%", totalBytes, totalSent, totalDelay);
            // }
            if (timeElapsed > durationOfTestInMillis) {
               break;
            }
         }
      }
   }
}
