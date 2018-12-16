package com.authrus.common.time;

import java.util.List;

public class CompositeRange implements TimeRange {

   private final List<TimeRange> ranges;

   public CompositeRange(List<TimeRange> ranges) {
      this.ranges = ranges;
   }

   @Override
   public boolean withinRange(long time) {
      for (TimeRange range : ranges) {
         if (range.withinRange(time)) {
            return true;
         }
      }
      return false;
   }
}
