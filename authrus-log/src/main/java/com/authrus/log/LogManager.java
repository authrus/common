package com.authrus.log;

import static com.authrus.log.LogLevel.INFO;

public final class LogManager {
   
   private static final Log LOG = new Log(INFO);     
   
   public static void log(LogLevel level, String tag, String message) {
      LogEvent event = new LogEvent(level, tag, message);
      
      if(message != null) {
         LOG.log(event);
      }
   }
   
   public static void log(LogLevel level, String tag, String message, Throwable throwable) {
      LogEvent event = new LogEvent(level, tag, message, throwable);
      
      if(message != null) {
         LOG.log(event);
      }
   }   
   
   public static void register(LogListener listener) {
      LOG.register(listener);
   }
   
   public static void remove(LogListener listener) {
      LOG.remove(listener);
   }
   
   public static void require(LogLevel level) {
      LOG.require(level);
   }
   
   public static boolean accept(LogLevel level) {
      return LOG.accept(level);
   }
}
