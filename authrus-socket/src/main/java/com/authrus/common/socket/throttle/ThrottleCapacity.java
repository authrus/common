package com.authrus.common.socket.throttle;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ThrottleCapacity {

   private final AtomicInteger alertPercentage;
   private final AtomicInteger freePercentage;
   private final AtomicLong totalCapacity;

   public ThrottleCapacity() {
      this(Long.MAX_VALUE);
   }

   public ThrottleCapacity(long totalCapacity) {
      this(totalCapacity, 90);
   }

   public ThrottleCapacity(long totalCapacity, int freePercentage) {
      this(totalCapacity, freePercentage, 75);
   }

   public ThrottleCapacity(long totalCapacity, int freePercentage, int alertPercentage) {
      this.alertPercentage = new AtomicInteger(alertPercentage);
      this.freePercentage = new AtomicInteger(freePercentage);
      this.totalCapacity = new AtomicLong(totalCapacity);

      if (freePercentage > 100) {
         throw new IllegalArgumentException("Free percentage of " + freePercentage + "% exceeds 100%");
      }
      if (alertPercentage > 100) {
         throw new IllegalArgumentException("Alert percentage of " + alertPercentage + "% exceeds 100%");
      }
   }

   public int getAlertPercentage() {
      return alertPercentage.get();
   }

   public void setAlertPercentage(int percentage) {
      this.alertPercentage.set(percentage);
   }

   public void setFreePercentage(int percentage) {
      freePercentage.set(percentage);
   }

   public int getFreePercentage() {
      return freePercentage.get();
   }

   public void setTotalBytes(long byteCount) {
      totalCapacity.set(byteCount);
   }

   public long getTotalBytes() {
      return totalCapacity.get();
   }
}
