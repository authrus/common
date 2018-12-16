package com.authrus.log;

public class ConsoleListener implements LogListener {
   
   private final boolean enabled;
   
   public ConsoleListener() {
      this(true);
   }
   
   public ConsoleListener(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   public void log(LogEvent event) {
      if(enabled) {
         long time = event.getTime();
         String thread = event.getThread();
         String message = event.getMessage();
         Throwable throwable = event.getThrowable();
         LogLevel level = event.getLevel();      
         String date = LogFormatter.formatLongDate(time);         
        
         System.err.print(level);
         System.err.print(" ");
         System.err.print(date);
         System.err.print(": ");
         System.err.print("[");
         System.err.print(thread);
         System.err.print("] ");
         System.err.println(message);
         
         if(throwable != null) {
            throwable.printStackTrace(System.err);
         }  
      }
   }
}
