package com.authrus.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class LogFormatter {

   private static final ThreadLocal<DateFormat> LONG_FORMAT = new ThreadLocal<DateFormat>() {

      @Override
      protected DateFormat initialValue() {
         return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      }
   };
   
   private static final ThreadLocal<DateFormat> SHORT_FORMAT = new ThreadLocal<DateFormat>() {

      @Override
      protected DateFormat initialValue() {
         return new SimpleDateFormat("yyyy-MM-dd");
      }
   };
   
   public static String formatShortDate(long date) {          
      try {         
         DateFormat format = SHORT_FORMAT.get();
         
         if(format == null) {
            throw new IllegalStateException("Could not create short format");
         }
         return format.format(date);
      } catch (Exception e) {
         throw new IllegalStateException("Unable to format '" + date + "'", e);
      }
   }   
   
   public static String formatLongDate(long date) {          
      try {         
         DateFormat format = LONG_FORMAT.get();
         
         if(format == null) {
            throw new IllegalStateException("Could not create long format");
         }
         return format.format(date);
      } catch (Exception e) {
         throw new IllegalStateException("Unable to format '" + date + "'", e);
      }
   }
}
