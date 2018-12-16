package com.authrus.log;

public enum LogLevel {
   TRACE(0),
   DEBUG(1),
   INFO(2),
   WARN(3),
   ERROR(4);
   
   public final int severity;
   
   private LogLevel(int severity) {
      this.severity = severity;
   }
   
   public int getSeverity() {
      return severity;
   }
}
