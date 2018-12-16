package com.authrus.common.time;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class DayTimeRangeTest extends TestCase {

   public void testDayTimeRange() throws Exception {
      DayTimeRange tuesdayRange = new DayTimeRange("Tuesday");
      DayTimeRange wednesdayRange = new DayTimeRange("Wed");
      SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
      Date tuesdayDate = format.parse("18/11/1977");
      Date wednesdayDate = format.parse("19/11/1977");
      long tuesdayTime = tuesdayDate.getTime();
      long wednesdayTime = wednesdayDate.getTime();
      
      assertTrue(tuesdayRange.withinRange(tuesdayTime));
      assertTrue(wednesdayRange.withinRange(wednesdayTime));
      assertFalse(tuesdayRange.withinRange(wednesdayTime));
      assertFalse(wednesdayRange.withinRange(tuesdayTime));   
   }
}
