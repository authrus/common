package com.authrus.common.time;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Represents a day that can be resolved from providing the current time
 * in milliseconds or a day token. This is useful when configuring jobs
 * or ranges that require the day of week rather than a specific date.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.common.time.DayTimeRange
 */
public enum Day {
   MONDAY(Calendar.MONDAY, "Monday", "Mon"),
   TUESDAY(Calendar.TUESDAY, "Tuesday", "Tue"),
   WEDNESDAY(Calendar.WEDNESDAY, "Wednesday", "Wed"),
   THURSDAY(Calendar.THURSDAY, "Thursday", "Thur"),
   FRIDAY(Calendar.FRIDAY, "Friday", "Fri"),
   SATURDAY(Calendar.SATURDAY, "Saturday", "Sat"),
   SUNDAY(Calendar.SUNDAY, "Sunday", "Sun");      
   
   private final String[] names;
   private final int code;
   
   private Day(int code, String... names) {
      this.names = names;
      this.code = code;
   }
   
   public String getName() {
      return names[0];
   }
   
   public String[] getNames() {
      return Arrays.copyOf(names, names.length);
   }
   
   public static Day resolveDay(long time) {
      DateTime date = DateTime.at(time);
      Calendar calendar = date.getCalendar();
      int code = calendar.get(Calendar.DAY_OF_WEEK);
      
      for(Day day : values()) {
         if(code == day.code) {               
            return day;               
         }
      }
      throw new IllegalStateException("Day could not be resolved from " + time);
   }
   
   public static Day resolveDay(String token) {
      if(token == null) {
         throw new IllegalArgumentException("Day must be specified");
      }
      for(Day day : values()) {
         for(String name : day.names) {
            if(name.equalsIgnoreCase(token)) {
               return day;
            }
         }
      }
      throw new IllegalArgumentException("Day " + token + " not found");
   }      
}
