package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.thread.ThreadPool;
import com.authrus.common.thread.ThreadPoolFactory;

@ManagedResource(description = "Collects samples from a sample analyser")
public class SampleCollector {

   private final RecordSampler recordSampler;
   private final ThreadFactory factory;
   private final Executor executor;

   public SampleCollector(SampleAnalyser sampleAnalyser, int sampleFrequency) {
      this.recordSampler = new RecordSampler(sampleAnalyser, sampleFrequency);
      this.factory = new ThreadPoolFactory(RecordSampler.class);
      this.executor = new ThreadPool(factory);
   }

   @ManagedOperation(description = "Starts sample collection")
   public void start() {
      recordSampler.start();
      executor.execute(recordSampler);
   }

   @ManagedOperation(description = "Stops sample collection")
   public void stop() {
      recordSampler.stop();
   }

   private static class RecordSampler implements Runnable {

      private final SampleAnalyser sampleAnalyser;
      private final AtomicBoolean collectorActive;
      private final long sampleFrequency;

      public RecordSampler(SampleAnalyser sampleAnalyser, int sampleFrequency) {
         this.collectorActive = new AtomicBoolean();
         this.sampleFrequency = sampleFrequency;
         this.sampleAnalyser = sampleAnalyser;
      }

      public void start() {
         collectorActive.set(true);
      }

      public void stop() {
         collectorActive.set(false);
      }

      @Override
      public void run() {
         while (collectorActive.get()) {
            try {
               Thread.sleep(sampleFrequency);
               sampleAnalyser.sample();
            } catch (Exception e) {
               continue;
            }
         }
      }
   }
}
