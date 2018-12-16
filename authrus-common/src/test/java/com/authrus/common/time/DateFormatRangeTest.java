package com.authrus.common.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class DateFormatRangeTest extends TestCase {

   public void testRange() throws Exception {
      DateFormat format = new SimpleDateFormat("HH:mm:ss");
      TimeRange range = new DateFormatRange("HH:mm:ss", "16:00:00", "16:30:00");
      Date date1 = format.parse("16:20:10");
      long time1 = date1.getTime();

      assertTrue("Time should be within range", range.withinRange(time1));

      Date date2 = format.parse("13:00:00");
      long time2 = date2.getTime();

      assertFalse("Time should be before start", range.withinRange(time2));

      Date date3 = format.parse("16:30:01");
      long time3 = date3.getTime();

      assertFalse("Time shoudl be after end", range.withinRange(time3));
   }
}
