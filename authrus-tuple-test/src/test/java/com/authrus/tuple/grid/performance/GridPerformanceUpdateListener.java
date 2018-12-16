package com.authrus.tuple.grid.performance;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;

public class GridPerformanceUpdateListener extends Thread implements TupleListener {

   private final AtomicInteger successes;
   private final AtomicInteger failures;
   private final AtomicInteger samples;
   private final AtomicLong sampleTime;
   private final AtomicLong totalTime;
   private final AtomicBoolean active;
   private final DecimalFormat format;
   private final String prefix;

   public GridPerformanceUpdateListener(String prefix, AtomicInteger successes, AtomicInteger failures) {
      this.format = new DecimalFormat("###,###,###,###");
      this.active = new AtomicBoolean(true);
      this.sampleTime = new AtomicLong();
      this.samples = new AtomicInteger();
      this.totalTime = new AtomicLong();
      this.successes = successes;
      this.failures = failures;
      this.prefix = prefix;
   }

   public void kill() {
      active.set(false);
   }

   public void run() {
      try {
         while (active.get()) {
            Thread.sleep(1000);               
            if(samples.get() > 0) {
               //long averageMicros = (totalTime.get() / successes.get());
               long sampleMillis = sampleTime.getAndSet(0);
               long sampleCount = samples.getAndSet(0);
               long averageMicros = (sampleMillis / sampleCount);               
               long averageMillis = TimeUnit.MICROSECONDS.toMillis(averageMicros);
               
               System.err.println(prefix + ": throughput=" + format.format(sampleCount) + " total=" + format.format(successes.get()) + " failures=" + format.format(failures.get()) + " latency-micros=" + averageMicros + " average-millis=" + averageMillis);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
         active.set(false);
      }
   }

   @Override
   public void onUpdate(Tuple tuple) {
      Map<String, Object> values = tuple.getAttributes();
      long sendTime = (Long)values.get("Time");
      long time = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sendTime);      
      
      successes.getAndIncrement();
      samples.getAndIncrement();
      totalTime.getAndAdd(time);
      sampleTime.getAndAdd(time);
   }

   @Override
   public void onException(Exception cause) {
      failures.getAndIncrement();
      System.err.print("onExeption(");
      cause.printStackTrace(System.err);         
      System.err.println(")");
   }

   @Override
   public void onHeartbeat() {
      System.err.println("onHeartbeat()");
   }

   @Override
   public void onReset() {
      System.err.println("onReset()");
   }

} 