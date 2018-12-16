package com.authrus.common.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatRange implements TimeRange {

   private final DateFormat dateFormat;
   private final Date startDate;
   private final Date stopDate;

   public DateFormatRange(String pattern, String start, String stop) throws Exception {
      this.dateFormat = new SimpleDateFormat(pattern);
      this.startDate = dateFormat.parse(start);
      this.stopDate = dateFormat.parse(stop);
   }

   @Override
   public boolean withinRange(long time) {
      long startTime = startDate.getTime();
      long stopTime = stopDate.getTime();

      return time >= startTime && time <= stopTime;
   }

}
