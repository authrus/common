package com.authrus.common.socket.throttle;

public class ThrottleMonitor {

   private final ThrottleClock throttleClock;
   private final Throttle readThrottle;
   private final Throttle writeThrottle;

   public ThrottleMonitor(ThrottleCapacity readCapacity, ThrottleCapacity writeCapacity) {
      this.throttleClock = new ThrottleClock();
      this.readThrottle = new ThrottleCalculator(readCapacity, throttleClock);
      this.writeThrottle = new ThrottleCalculator(writeCapacity, throttleClock);
   }

   public ThrottleClock getThrottleClock() {
      return throttleClock;
   }

   public Throttle getReadThrottle() {
      return readThrottle;
   }

   public Throttle getWriteThrottle() {
      return writeThrottle;
   }
}
