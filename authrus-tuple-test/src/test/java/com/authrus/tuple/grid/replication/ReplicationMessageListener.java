package com.authrus.tuple.grid.replication;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;

public class ReplicationMessageListener extends Thread implements TupleListener {

   private final Map<String, Tuple> messages;
   private final AtomicInteger successes;
   private final AtomicInteger failures;
   private final AtomicInteger samples;
   private final AtomicLong sampleTime;
   private final AtomicLong totalTime;
   private final AtomicBoolean active;
   private final DecimalFormat format;
   private final String prefix;

   public ReplicationMessageListener(Map<String, Tuple> messages, String prefix, AtomicInteger successes, AtomicInteger failures) {
      this.format = new DecimalFormat("###,###,###,###");
      this.active = new AtomicBoolean(true);
      this.sampleTime = new AtomicLong();
      this.samples = new AtomicInteger();
      this.totalTime = new AtomicLong();
      this.messages = messages;
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
      String name = (String)values.get("name");
      long sendTime = (Long)values.get("time");
      long time = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sendTime); 
      
      //System.err.println(prefix + ": " + message);
      
      messages.put(name, tuple);
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
