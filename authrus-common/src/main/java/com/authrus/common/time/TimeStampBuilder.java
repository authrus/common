package com.authrus.common.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampBuilder {
   
   public static final String DEFAULT_PATTERN = "yyyy_MM_EEE_dd_HH_mm_ss"; 
   
   private final String pattern;
   
   public TimeStampBuilder() {
      this(DEFAULT_PATTERN);
   }
   
   public TimeStampBuilder(String pattern) {
      this.pattern = pattern;
   }
   
   public String createTimeStamp() {
      DateFormat format = new SimpleDateFormat(pattern);
      Date date = new Date();
      
      return format.format(date);
   }
}
