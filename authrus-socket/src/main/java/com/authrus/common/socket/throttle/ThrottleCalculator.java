package com.authrus.common.socket.throttle;

public class ThrottleCalculator implements Throttle {

   private final ThrottleCapacity throttleCapacity;
   private final ThrottleClock throttleClock;
   private final TimeSlot[] rollingMinute;

   public ThrottleCalculator(ThrottleCapacity throttleCapacity, ThrottleClock throttleClock) {
      this.rollingMinute = new TimeSlot[600];
      this.throttleCapacity = throttleCapacity;
      this.throttleClock = throttleClock;
   }

   public ThrottleResult update(long packetBytes) {
      double freePercentage = throttleCapacity.getFreePercentage();
      double freeRatio = freePercentage * 0.01;
      long totalBytes = throttleCapacity.getTotalBytes();
      long freeBytes = Math.round(totalBytes * freeRatio);
      long usedBytes = updateUsedCapacity(packetBytes);
      long throttleDelay = calculateDelay(totalBytes, freeBytes, usedBytes, packetBytes);

      return new ThrottleResult(throttleCapacity, throttleDelay, usedBytes);
   }

   public long calculateDelay(long totalBytes, long freeBytes, long usedBytes, long packetBytes) {
      long remainingBytes = totalBytes - usedBytes;

      if (usedBytes > totalBytes) {
         return calculateReductionDelay(-remainingBytes);
      }
      if (usedBytes > freeBytes) {
         return calculateExpansionDelay(remainingBytes, packetBytes);
      }
      return 0;
   }

   public long calculateReductionDelay(long reduceBytes) {
      long currentTime = throttleClock.currentTime();
      long currentMinute = currentTime % 60000;
      long currentSlot = currentMinute / 100;
      long expiryTime = currentTime - 60000;
      long throttleDelay = 100;

      for (int i = 0; i < rollingMinute.length; i++) {
         if (currentSlot == i) {
            for (int j = 0; j < rollingMinute.length; j++) {
               int rotatingIndex = (i + j) % rollingMinute.length;

               if (rollingMinute[rotatingIndex] != null) {
                  if (rollingMinute[i].creationTime > expiryTime) {
                     long byteCount = rollingMinute[rotatingIndex].byteCount;

                     if (reduceBytes - byteCount <= 0) {
                        return throttleDelay;
                     }
                     reduceBytes -= byteCount;
                     throttleDelay += 100;
                  }
               }
            }
         }
      }
      return throttleDelay;
   }

   public long calculateExpansionDelay(long remainingBytes, long packetBytes) {
      double slotsInMinute = rollingMinute.length;
      double slotBytes = remainingBytes / slotsInMinute;
      double slotCount = packetBytes / slotBytes;

      return Math.round(slotCount * 100.0);
   }

   public long updateUsedCapacity(long payloadBytes) {
      long currentTime = throttleClock.currentTime();
      long currentMinute = currentTime % 60000;
      long currentSlot = currentMinute / 100;
      long expiryTime = currentTime - 60000;
      long countThisMinute = 0;
      int updateCount = 1;

      for (int i = 0; i < rollingMinute.length; i++) {
         if (currentSlot == i) {
            if (rollingMinute[i] != null) {
               if (currentTime - rollingMinute[i].creationTime <= 100) {
                  payloadBytes += rollingMinute[i].byteCount;
                  updateCount += rollingMinute[i].updateCount;
               }
            }
            rollingMinute[i] = new TimeSlot(payloadBytes, currentTime, updateCount);
         }
         if (rollingMinute[i] != null) {
            if (rollingMinute[i].creationTime > expiryTime) {
               countThisMinute += rollingMinute[i].byteCount;
            }
         }
      }
      return countThisMinute;
   }

   private class TimeSlot {

      public final long creationTime;
      public final long byteCount;
      public final int updateCount;

      public TimeSlot(long byteCount, long creationTime, int updateCount) {
         this.creationTime = creationTime;
         this.updateCount = updateCount;
         this.byteCount = byteCount;
      }
   }
}
