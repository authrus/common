package com.authrus.common.socket.throttle;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.thread.ThreadPool;

public class ThrottleAlarmDispatcher implements ThrottleAlarm {

   private static final Logger LOG = LoggerFactory.getLogger(ThrottleAlarmDispatcher.class);

   private final List<ThrottleAlarm> alarms;
   private final AtomicInteger pending;
   private final AtomicInteger ignore;
   private final AtomicInteger count;
   private final Executor executor;
   private final int capacity;

   public ThrottleAlarmDispatcher(List<ThrottleAlarm> alarms) {
      this(alarms, 100);
   }

   public ThrottleAlarmDispatcher(List<ThrottleAlarm> alarms, int capacity) {
      this.pending = new AtomicInteger();
      this.ignore = new AtomicInteger();
      this.count = new AtomicInteger();
      this.executor = new ThreadPool(1);
      this.capacity = capacity;
      this.alarms = alarms;
   }

   @Override
   public void raiseInputAlarm(ThrottleEvent event) {
      ThrottleTask task = new ThrottleTask(event, ThrottleType.INPUT);

      if (pending.get() < capacity) {
         pending.getAndIncrement();
         count.getAndIncrement();
         executor.execute(task);
      } else {
         ignore.getAndIncrement();
      }
   }

   @Override
   public void raiseOutputAlarm(ThrottleEvent event) {
      ThrottleTask task = new ThrottleTask(event, ThrottleType.OUTPUT);

      if (pending.get() < capacity) {
         pending.getAndIncrement();
         count.getAndIncrement();
         executor.execute(task);
      } else {
         ignore.getAndIncrement();
      }
   }

   private class ThrottleTask implements Runnable {

      private final ThrottleEvent event;
      private final ThrottleType type;

      private ThrottleTask(ThrottleEvent event, ThrottleType type) {
         this.event = event;
         this.type = type;
      }

      public void run() {
         try {
            for (ThrottleAlarm alarm : alarms) {
               try {
                  type.dispatchAlarm(alarm, event);
               } catch (Exception e) {
                  LOG.info("Error raising alarm", e);
               }
            }
         } finally {
            pending.getAndDecrement();
         }

      }
   }

   private enum ThrottleType {
      INPUT {
         public void dispatchAlarm(ThrottleAlarm alarm, ThrottleEvent event) {
            alarm.raiseInputAlarm(event);
         }
      },
      OUTPUT {
         public void dispatchAlarm(ThrottleAlarm alarm, ThrottleEvent event) {
            alarm.raiseOutputAlarm(event);
         }
      };

      public abstract void dispatchAlarm(ThrottleAlarm alarm, ThrottleEvent event);
   }
}
