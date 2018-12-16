package com.authrus.log;

public class LogEvent {
   
   private final Throwable throwable;
   private final String message;
   private final LogLevel level;
   private final String thread;
   private final String tag;
   private final long time;
   
   public LogEvent(LogLevel level, String tag, String message) {
      this(level, tag, message, null);
   }
   
   public LogEvent(LogLevel level, String tag, String message, Throwable throwable) {
      this.thread = Thread.currentThread().getName();
      this.time = System.currentTimeMillis();
      this.throwable = throwable;
      this.message = message;
      this.level = level;
      this.tag = tag;
   }  

   public Throwable getThrowable() {
      return throwable;
   }

   public String getMessage() {
      return message;
   }

   public LogLevel getLevel() {
      return level;
   }

   public String getThread() {
      return thread;
   }
   
   public String getTag() {
      return tag;
   }

   public long getTime() {
      return time;
   }
}
