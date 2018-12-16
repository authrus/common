package com.authrus.common.log;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import com.authrus.common.log.LogEventCollector;

public class LogEventCollectorTest {

   @Test
   public void testLogger() {
      LogEventCollector logger = new LogEventCollector("%d [%t] %-5p %c{2} - %m%n", 10);

      for (int i = 0; i < 20; i++) {
         LoggingEvent event = new LoggingEvent(LogEventCollectorTest.class.getName(), Logger.getRootLogger(), System.currentTimeMillis(), Level.INFO, String.valueOf(i), Thread.currentThread()
               .getName(), null, null, null, null);

         logger.append(event);
      }
      List<LoggingEvent> loggingEvents = logger.getLoggingEvents();

      assertEquals(loggingEvents.size(), 10);
      assertEquals(loggingEvents.get(0).getMessage(), "10");
      assertEquals(loggingEvents.get(1).getMessage(), "11");
      assertEquals(loggingEvents.get(2).getMessage(), "12");
      assertEquals(loggingEvents.get(3).getMessage(), "13");
      assertEquals(loggingEvents.get(4).getMessage(), "14");
      assertEquals(loggingEvents.get(5).getMessage(), "15");
      assertEquals(loggingEvents.get(6).getMessage(), "16");
      assertEquals(loggingEvents.get(7).getMessage(), "17");
      assertEquals(loggingEvents.get(8).getMessage(), "18");
      assertEquals(loggingEvents.get(9).getMessage(), "19");
   }

   @Test
   public void testFilter() {
      LogEventCollector logger = new LogEventCollector("%d [%t] %-5p %c{2} - %m%n", 10);

      logger.setEventFilter("Test (1|2|3|4)");

      for (int i = 0; i < 10; i++) {
         LoggingEvent event = new LoggingEvent(LogEventCollectorTest.class.getName(), Logger.getRootLogger(), System.currentTimeMillis(), Level.INFO, String.format("Test %s event logged", i), Thread
               .currentThread().getName(), null, null, null, null);

         logger.append(event);
      }
      List<LoggingEvent> loggingEvents = logger.getLoggingEvents();

      assertEquals(loggingEvents.size(), 4);
      assertEquals(loggingEvents.get(0).getMessage(), "Test 1 event logged");
      assertEquals(loggingEvents.get(1).getMessage(), "Test 2 event logged");
      assertEquals(loggingEvents.get(2).getMessage(), "Test 3 event logged");
      assertEquals(loggingEvents.get(3).getMessage(), "Test 4 event logged");
   }

}
