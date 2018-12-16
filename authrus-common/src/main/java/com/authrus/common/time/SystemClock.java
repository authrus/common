package com.authrus.common.time;

public class SystemClock implements Clock {

   @Override
   public Time currentTime() {
      long timeInMillis = System.currentTimeMillis();
      long timeInNanos = System.nanoTime();

      return new Time(timeInMillis, timeInNanos);
   }
}
