package com.authrus.log;

import java.util.List;

public class LogAdministrator {
   
   private final List<LogListener> appenders;
   
   public LogAdministrator(List<LogListener> appenders) {
      this.appenders = appenders;
   }
   
   public void start() {
      for(LogListener appender : appenders) {
         LogManager.register(appender);
      }
   }
   
   public void stop() {
      for(LogListener appender : appenders) {
         LogManager.remove(appender);
      }      
   }
}
