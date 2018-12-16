package com.authrus.common.time;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date and time class used to represent an immutable date and time value. This
 * contains various convenience methods to simplify date and time manipulation.
 * Each date and time also comes with a time zone to ensure times can be
 * represented across various regions.
 * 
 * @author Niall Gallagher
 */
public class DateTime implements Serializable, Comparable<DateTime> {

   private static final long serialVersionUID = 1L;

   private final Calendar dateTime;

   private DateTime(Calendar dateTime) {
      this.dateTime = dateTime;
   }

   public long getTime() {
      return dateTime.getTimeInMillis();
   }

   public Date getDate() {
      return dateTime.getTime();
   }

   public TimeZone getTimeZone() {
      return dateTime.getTimeZone();
   }

   public Calendar getCalendar() {
      TimeZone timeZone = getTimeZone();
      Calendar calendar = Calendar.getInstance(timeZone);
      Date date = getDate();
      calendar.setTime(date);
      return calendar;
   }

   public int getYear() {
      return dateTime.get(Calendar.YEAR);
   }

   public int getMonth() {
      return 1 + dateTime.get(Calendar.MONTH);
   }

   public int getDay() {
      return dateTime.get(Calendar.DAY_OF_MONTH);
   }

   public int getHour() {
      return dateTime.get(Calendar.HOUR_OF_DAY);
   }

   public int getMinute() {
      return dateTime.get(Calendar.MINUTE);
   }

   public int getSecond() {
      return dateTime.get(Calendar.SECOND);
   }

   public DateTime minusSeconds(int seconds) {
      return addSeconds(-seconds);
   }

   public DateTime minusMinutes(int minutes) {
      return addMinutes(-minutes);
   }

   public DateTime minusHours(int hours) {
      return addHours(-hours);
   }

   public DateTime minusDays(int days) {
      return addDays(-days);
   }

   public DateTime minusMonths(int months) {
      return addDays(-months);
   }

   public DateTime minusYears(int years) {
      return addDays(-years);
   }

   public DateTime addSeconds(int seconds) {
      return add(seconds, Calendar.SECOND);
   }

   public DateTime addMinutes(int minutes) {
      return add(minutes, Calendar.MINUTE);
   }

   public DateTime addHours(int hours) {
      return add(hours, Calendar.HOUR);
   }

   public DateTime addDays(int days) {
      return add(days, Calendar.DAY_OF_YEAR);
   }

   public DateTime addMonths(int months) {
      return add(months, Calendar.MONTH);
   }

   public DateTime addYears(int years) {
      return add(years, Calendar.YEAR);
   }

   private DateTime add(int count, int unit) {
      Calendar calendar = getCalendar();
      calendar.add(unit, count);
      return new DateTime(calendar);
   }

   public String formatDate(String pattern) {
      DateFormat format = new SimpleDateFormat(pattern);
      return formatDate(format);
   }

   public String formatDate(DateFormat format) {
      return format.format(getDate());
   }

   public Duration timeElapsed() {
      long currentTime = System.currentTimeMillis();
      return timeDifference(currentTime);
   }

   public Duration timeDifference(DateTime otherTime) {
      return timeDifference(otherTime.getTime());
   }

   public Duration timeDifference(long otherTime) {
      long thisTime = getTime();
      long diff = Math.abs(thisTime - otherTime);
      long millis = diff % 1000;
      long seconds = (diff / 1000) % 60;
      long minutes = (diff / 60000) % 60;
      long hours = (diff / 3600000) % 24;
      long days = (diff / 86400000);
      return new Duration(diff, days, hours, minutes, seconds, millis);
   }

   public boolean before(DateTime time) {
      return getTime() < time.getTime();
   }

   public boolean after(DateTime time) {
      return getTime() > time.getTime();
   }

   public boolean equals(Object value) {
      if (value instanceof DateTime) {
         DateTime time = (DateTime) value;
         return equals(time);
      }
      return false;
   }

   public boolean equals(DateTime time) {
      return dateTime.equals(time.dateTime);
   }

   public int hashCode() {
      return dateTime.hashCode();
   }

   public int compareTo(DateTime time) {
      return dateTime.compareTo(time.dateTime);
   }

   public String toString() {
      return timeElapsed().toString();
   }

   public static DateTime now() {
      Calendar dateTime = Calendar.getInstance();
      return new DateTime(dateTime);
   }

   public static DateTime now(TimeZone timeZone) {
      Calendar dateTime = Calendar.getInstance(timeZone);
      return new DateTime(dateTime);
   }

   public static DateTime at(long timeInMillis) {
      Calendar dateTime = Calendar.getInstance();
      dateTime.setTimeInMillis(timeInMillis);
      return new DateTime(dateTime);
   }

   public static DateTime at(long timeInMillis, TimeZone timeZone) {
      Calendar dateTime = Calendar.getInstance(timeZone);
      dateTime.setTimeInMillis(timeInMillis);
      return new DateTime(dateTime);
   }

   public static DateTime at(Date date) {
      Calendar dateTime = Calendar.getInstance();
      dateTime.setTime(date);
      return new DateTime(dateTime);
   }

   public static DateTime at(Date date, TimeZone timeZone) {
      Calendar dateTime = Calendar.getInstance(timeZone);
      dateTime.setTime(date);
      return new DateTime(dateTime);
   }

   public static DateTime at(int day, int month, int year) {
      Calendar dateTime = Calendar.getInstance();
      dateTime.set(Calendar.DAY_OF_MONTH, day);
      dateTime.set(Calendar.MONTH, month - 1);
      dateTime.set(Calendar.YEAR, year);
      return new DateTime(dateTime);
   }

   public static DateTime at(int day, int month, int year, TimeZone timeZone) {
      Calendar dateTime = Calendar.getInstance(timeZone);
      dateTime.set(Calendar.DAY_OF_MONTH, day);
      dateTime.set(Calendar.MONTH, month - 1);
      dateTime.set(Calendar.YEAR, year);
      return new DateTime(dateTime);
   }

   public static class Duration implements Comparable<Duration> {

      private long diff;
      private long days;
      private long hours;
      private long minutes;
      private long seconds;
      private long millis;

      public Duration(long diff, long days, long hours, long minutes, long seconds, long millis) {
         this.diff = diff;
         this.days = days;
         this.hours = hours;
         this.minutes = minutes;
         this.seconds = seconds;
         this.millis = millis;
      }

      public long getDifference() {
         return diff;
      }

      public long getDays() {
         return days;
      }

      public long getHours() {
         return hours;
      }

      public long getMinutes() {
         return minutes;
      }

      public long getSeconds() {
         return seconds;
      }

      public long getMillis() {
         return millis;
      }

      public int compareTo(Duration duration) {
         if (diff < duration.diff) {
            return -1;
         }
         if (diff == duration.diff) {
            return 0;
         }
         return 1;
      }

      public String toString() {
         StringBuilder builder = new StringBuilder();
         
         if (days > 0) {
            builder.append(days);
            builder.append(" days ");
         }
         if (days > 0 || hours > 0) {
            builder.append(hours);
            builder.append(" hours ");
         }
         if (days > 0 || hours > 0 || minutes > 0) {
            builder.append(minutes);
            
            if(days > 0) {
               builder.append(" minutes");
            } else {
               builder.append(" minutes "); 
            }
         }
         if (days <= 0) {
            builder.append(seconds);
            builder.append(" seconds");
         }
         return builder.toString();
      }
   }
}
