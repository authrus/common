package com.authrus.common.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.collections.LeastRecentlyUsedMap;

@ManagedResource(description="Appender that enables recent log messages to be viewed by JMX")
public class LogEventCollector extends LogEventFilter {

   private final LeastRecentlyUsedMap<Long, LoggingEvent> recentEvents;
   private final AtomicInteger eventSequenceId;
   private final AtomicInteger failureCount;
   private final PatternLayout patternLayout;
   private final int eventCapacity;

   public LogEventCollector(String pattern) {
      this(pattern, 200);
   }

   public LogEventCollector(String pattern, int eventCapacity) {
      this.recentEvents = new LeastRecentlyUsedMap<Long, LoggingEvent>(eventCapacity);
      this.patternLayout = new PatternLayout(pattern);
      this.eventSequenceId = new AtomicInteger();
      this.failureCount = new AtomicInteger();
      this.eventCapacity = eventCapacity;
   }

   @ManagedOperation(description="Show recent logging events")
   public synchronized String showRecentEvents() {
      StringWriter buffer = new StringWriter();
      PrintWriter writer = new PrintWriter(buffer);
      
      if(!recentEvents.isEmpty()) {
         Set<Long> eventIds = recentEvents.keySet();

         writer.write("<pre>");

         for (Long sequenceId : eventIds) {
            LoggingEvent loggingEvent = recentEvents.get(sequenceId);
            String formattedEvent = patternLayout.format(loggingEvent);
            ThrowableInformation information = loggingEvent.getThrowableInformation();
            
            writer.write(formattedEvent);
            
            if(information != null) {
               Throwable cause = information.getThrowable();
               
               writer.flush();
               cause.printStackTrace(writer);               
            }
         }
         writer.write("</pre>");
         writer.flush();
      }
      return buffer.toString();
   }

   @ManagedOperation(description="Clears the recent events")
   public synchronized void clearRecentEvents() {
      recentEvents.clear();
   }

   public synchronized List<LoggingEvent> getLoggingEvents() {
      List<LoggingEvent> loggingEvents = new ArrayList<LoggingEvent>();

      for (Long time : recentEvents.keySet()) {
         LoggingEvent event = recentEvents.get(time);
         loggingEvents.add(event);
      }
      return loggingEvents;
   }

   @Override
   protected synchronized void append(LoggingEvent event) {
      try {
         Pattern eventFilter = getFilterPattern();
         long sequenceId = eventSequenceId.getAndIncrement();

         if (eventFilter != null) {
            String formattedEvent = patternLayout.format(event);
            Matcher matcher = eventFilter.matcher(formattedEvent);

            if (matcher.matches()) {
               recentEvents.put(sequenceId, event);
            }
         } else {
            recentEvents.put(sequenceId, event);
         }
      } catch (Throwable e) {
         failureCount.getAndIncrement();
      }
   }

   @Override
   public synchronized boolean requiresLayout() {
      return false;
   }

   @Override
   public synchronized void close() {
      recentEvents.clear();
   }

}
