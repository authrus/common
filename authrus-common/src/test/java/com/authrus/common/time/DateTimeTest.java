package com.authrus.common.time;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.authrus.common.time.DateTime.Duration;

public class DateTimeTest {

   @Test
   public void testMillis() throws Exception {
      long currentTime = System.currentTimeMillis();
      long compareTime = currentTime + 102;
      long longerTime = currentTime + 103;
      DateTime currentDateTime = DateTime.at(currentTime);
      DateTime compareDateTime = DateTime.at(compareTime);
      DateTime longerDateTime = DateTime.at(longerTime);
      Duration duration = currentDateTime.timeDifference(compareDateTime);
      Duration longerDuration = currentDateTime.timeDifference(longerDateTime);

      assertEquals(duration.getDays(), 0);
      assertEquals(duration.getHours(), 0);
      assertEquals(duration.getMinutes(), 0);
      assertEquals(duration.getSeconds(), 0);
      assertEquals(duration.getMillis(), 102);
      assertEquals(longerDuration.getMillis(), 103);
      assertEquals(duration.compareTo(duration), 0);
      assertEquals(longerDuration.compareTo(duration), 1);
      assertEquals(duration.compareTo(longerDuration), -1);
   }

   @Test
   public void testMonths() throws Exception {
      DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = format.parse("01/01/2012 00:00:00");
      DateTime dateTime = DateTime.at(date.getTime());

      assertEquals(dateTime.getDay(), 1);
      assertEquals(dateTime.getMonth(), 1);
      assertEquals(dateTime.getYear(), 2012);
      assertEquals(dateTime.getHour(), 0);
      assertEquals(dateTime.getMinute(), 0);
      assertEquals(dateTime.getSecond(), 0);
   }

   @Test
   public void testDays() throws Exception {
      DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = format.parse("01/01/2012 00:00:00");
      DateTime dateTime = DateTime.at(date.getTime());
      Duration duration = dateTime.timeDifference(date.getTime());

      assertEquals(duration.toString(), "0 seconds");

      dateTime = dateTime.addYears(2);
      duration = dateTime.timeDifference(date.getTime());

      assertEquals(dateTime.formatDate(format), "01/01/2014 00:00:00");
      assertEquals(duration.toString(), "731 days 0 hours 0 minutes");

      dateTime = dateTime.addHours(24);
      duration = dateTime.timeDifference(date.getTime());

      assertEquals(dateTime.formatDate(format), "02/01/2014 00:00:00");
      assertEquals(duration.toString(), "732 days 0 hours 0 minutes");

      dateTime = dateTime.addHours(24);
      duration = dateTime.timeDifference(date.getTime());

      assertEquals(dateTime.formatDate(format), "03/01/2014 00:00:00");
      assertEquals(duration.toString(), "733 days 0 hours 0 minutes");
   }

   @Test
   public void testDateTime() throws Exception {
      DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date1 = format.parse("01/01/2012 05:30:00");
      Date date2 = format.parse("01/01/2012 05:31:10");
      Date date3 = format.parse("01/01/2012 07:31:10");

      DateTime time1 = DateTime.at(date1.getTime());
      Duration duration1 = time1.timeDifference(date2.getTime());
      Duration duration2 = time1.timeDifference(date3.getTime());

      assertEquals(duration1.toString(), "1 minutes 10 seconds");
      assertEquals(duration2.toString(), "2 hours 1 minutes 10 seconds");

      System.err.printf("time1=%s time2=%s%n", time1.getTime(), date2.getTime());
      System.err.printf("time1=%s time2=%s%n", time1.formatDate(format), DateTime.at(date2.getTime()).formatDate(format));

      time1 = time1.addMinutes(1);
      duration1 = time1.timeDifference(date2.getTime());

      System.err.printf("time1=%s time2=%s%n", time1.getTime(), date2.getTime());
      System.err.printf("time1=%s time2=%s%n", time1.formatDate(format), DateTime.at(date2.getTime()).formatDate(format));

      assertEquals(duration1.toString(), "10 seconds");

      time1 = time1.addSeconds(10);
      duration1 = time1.timeDifference(date2.getTime());
      System.err.println("adding 10 seconds time1=" + time1.formatDate(format));

      System.err.println(duration1);
      assertEquals(duration1.toString(), "0 seconds");

      time1 = time1.addDays(3);
      duration1 = time1.timeDifference(date2.getTime());
      System.err.println("adding 3 days to get time1=" + time1.formatDate(format));

      System.err.println(duration1);
      assertEquals(duration1.toString(), "3 days 0 hours 0 minutes");

      time1 = time1.addSeconds(11);
      duration1 = time1.timeDifference(date2.getTime());
      System.err.println("adding 11 seconds");

      System.err.println(duration1);
      assertEquals(duration1.toString(), "3 days 0 hours 0 minutes");

      System.err.println("time1=" + time1.formatDate(format));
      time1 = time1.addHours(12);
      System.err.println("time1=" + time1.formatDate(format));
      duration1 = time1.timeDifference(date2.getTime());

      System.err.println(duration1);
      assertEquals(duration1.toString(), "3 days 12 hours 0 minutes");
   }
}
