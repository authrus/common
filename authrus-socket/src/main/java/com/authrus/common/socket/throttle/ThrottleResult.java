package com.authrus.common.socket.throttle;

public class ThrottleResult {

   private final ThrottleCapacity capacity;
   private final long throttleDelay;
   private final long quotaUsed;

   public ThrottleResult(ThrottleCapacity capacity, long throttleDelay, long quotaUsed) {
      this.throttleDelay = throttleDelay;
      this.quotaUsed = quotaUsed;
      this.capacity = capacity;
   }

   public ThrottleStatus getResultStatus() {
      long freePercentage = getFreePercentageUsed();

      if (freePercentage >= 100) {
         return ThrottleStatus.ON;
      }
      return ThrottleStatus.OFF;
   }

   public long getFreePercentageUsed() {
      double freeCapacity = getFreeCapacity();

      if (freeCapacity > quotaUsed) {
         double freeRatio = quotaUsed / freeCapacity;
         long freePercentage = Math.round(freeRatio * 100.0);

         return Math.min(freePercentage, 100);
      }
      return 100;
   }

   public long getTotalPercentageUsed() {
      double totalCapacity = getTotalCapacity();

      if (totalCapacity > quotaUsed) {
         double totalRatio = quotaUsed / totalCapacity;
         long totalPercentage = Math.round(totalRatio * 100.0);

         return Math.min(totalPercentage, 100);
      }
      return 100;
   }

   public long getFreeCapacity() {
      double totalCapacity = getTotalCapacity();
      double freePercentage = getFreePercentage();

      return Math.round(totalCapacity * freePercentage * 0.01);
   }

   public long getAlertPercentage() {
      return capacity.getAlertPercentage();
   }

   public long getFreePercentage() {
      return capacity.getFreePercentage();
   }

   public long getTotalCapacity() {
      return capacity.getTotalBytes();
   }

   public long getUsedCapacity() {
      return quotaUsed;
   }

   public long getThrottleDelay() {
      return throttleDelay;
   }
}
