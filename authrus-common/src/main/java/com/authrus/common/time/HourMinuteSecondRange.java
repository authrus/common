package com.authrus.common.time;

public class HourMinuteSecondRange implements TimeRange {

   private final HourMinuteSecond startTime;
   private final HourMinuteSecond stopTime;

   public HourMinuteSecondRange(String startTime, String stopTime) throws Exception {
      this.startTime = new HourMinuteSecond(startTime);
      this.stopTime = new HourMinuteSecond(stopTime);
   }

   @Override
   public boolean withinRange(long timeInMillis) {
      DateTime time = DateTime.at(timeInMillis);

      if (afterStart(time) && beforeStop(time)) {
         return true;
      }
      return false;
   }

   private boolean beforeStop(DateTime time) {
      int startHour = stopTime.getHour();
      int startMinute = stopTime.getMinute();
      int startSecond = stopTime.getSecond();
      int hour = time.getHour();
      int minute = time.getMinute();
      int second = time.getSecond();

      if (startHour < hour) {
         return false;
      }
      if (startHour == hour) {
         if (startMinute < minute) {
            return false;
         }
         if (startMinute == minute) {
            if (startSecond < second) {
               return false;
            }
         }
      }
      return true;
   }

   private boolean afterStart(DateTime time) {
      int startHour = startTime.getHour();
      int startMinute = startTime.getMinute();
      int startSecond = startTime.getSecond();
      int hour = time.getHour();
      int minute = time.getMinute();
      int second = time.getSecond();

      if (startHour > hour) {
         return false;
      }
      if (startHour == hour) {
         if (startMinute > minute) {
            return false;
         }
         if (startMinute == minute) {
            if (startSecond > second) {
               return false;
            }
         }
      }
      return true;
   }

   private static class HourMinuteSecond {

      private String[] parts;
      private String time;
      private int hour;
      private int minute;
      private int second;

      public HourMinuteSecond(String time) {
         this.parts = time.split(":");
         this.time = time;
         this.minute = -1;
         this.second = -1;
         this.hour = -1;
      }

      public int getHour() {
         if (parts.length != 3) {
            throw new IllegalStateException("Time specified as '" + time + "' must be HH:MM:SS format");
         }
         if (hour < 0) {
            hour = Integer.parseInt(parts[0]);

            if (hour < 0 || hour > 23) {
               throw new IllegalStateException("Hour '" + parts[0] + "' not in the 24 hour range for " + time);
            }
         }
         return hour;
      }

      public int getMinute() {
         if (parts.length != 3) {
            throw new IllegalStateException("Time specified as '" + time + "' must be HH:MM:SS format");
         }
         if (minute < 0) {
            minute = Integer.parseInt(parts[1]);

            if (minute < 0 || minute > 59) {
               throw new IllegalStateException("Minute '" + parts[1] + "' not in the 24 hour range for " + time);
            }
         }
         return minute;
      }

      public int getSecond() {
         if (parts.length != 3) {
            throw new IllegalStateException("Time specified as '" + time + "' must be HH:MM:SS format");
         }
         if (second < 0) {
            second = Integer.parseInt(parts[2]);

            if (second < 0 || second > 59) {
               throw new IllegalStateException("Minute '" + parts[2] + "' not in the 24 hour range for " + time);
            }
         }
         return second;
      }
   }
}
