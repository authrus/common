package com.authrus.log;

import static com.authrus.log.LogLevel.INFO;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public class Log {

   private final AtomicReference<LogLevel> limit;
   private final Set<LogListener> listeners;

   public Log() {
      this(INFO);
   }
   
   public Log(LogLevel level) {
      this.listeners = new CopyOnWriteArraySet<LogListener>();
      this.limit = new AtomicReference<LogLevel>(level);
   }
   
   public void register(LogListener listener) {
      listeners.add(listener);
   }
   
   public void remove(LogListener listener) {
      listeners.remove(listener);
   }
   
   public void require(LogLevel level) {
      if(level != null) {
         limit.set(level);
      }
   }
   
   public boolean accept(LogLevel level) {
      LogLevel require = limit.get();
      
      if(require != null) {
         return require.severity <= level.severity;
      }
      return true;
   }
   
   public void log(LogEvent event) {
      LogLevel level = event.getLevel();
      
      if(accept(level)) {
         for(LogListener listener : listeners) {
            try {
               listener.log(event);
            } catch(Exception e) {
               listeners.remove(listener);
            }
         }
      }
   }
}
