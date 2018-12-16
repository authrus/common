package com.authrus.common.time;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeTest {

   @Test
   public void timeMillisTime() throws Exception {
      Time earlyTime = new Time(1);
      Time lateTime = new Time(2);

      assertTrue(lateTime.after(earlyTime));
      assertTrue(earlyTime.before(lateTime));
      assertTrue(earlyTime.sameTime(earlyTime));
      assertTrue(lateTime.sameTime(lateTime));
   }
}
