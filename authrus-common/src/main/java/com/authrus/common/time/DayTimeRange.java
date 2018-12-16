package com.authrus.common.time;

public class DayTimeRange implements TimeRange {
   
   private final Day day;
   
   public DayTimeRange(String day) {
      this.day = Day.resolveDay(day);
   }

   @Override
   public boolean withinRange(long time) {
      return day == Day.resolveDay(time);
   }
}
