package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.authrus.common.socket.proxy.analyser.PacketAnalyser;
import com.authrus.common.socket.proxy.analyser.Packet;
import com.authrus.common.time.SampleAverager;
import com.authrus.common.time.Time;

public class SampleAnalyser implements PacketAnalyser {

   private final Map<String, RecordSampler> recordSamplers;
   private final Set<String> knownConnections;
   private final SampleRecorder sampleRecorder;

   public SampleAnalyser(SampleRecorder sampleRecorder) {
      this.recordSamplers = new ConcurrentHashMap<String, RecordSampler>();
      this.knownConnections = new CopyOnWriteArraySet<String>();
      this.sampleRecorder = sampleRecorder;
   }

   @Override
   public void analyse(Time startTime, Time endTime, Packet packetData) {
      String connection = packetData.connection();
      RecordSampler recordSampler = resolve(connection);

      if (recordSampler != null) {
         long startTimeMillis = startTime.getMillisTime();
         long endTimeMillis = endTime.getMillisTime();
         int payloadSize = packetData.length();

         recordSampler.update(endTimeMillis - startTimeMillis, payloadSize);
      }
   }

   private RecordSampler resolve(String connection) {
      RecordSampler statistics = recordSamplers.get(connection);

      if (statistics == null) {
         statistics = new RecordSampler(connection);

         recordSamplers.put(connection, statistics);
         knownConnections.add(connection);
      }
      return statistics;
   }

   public void sample() {
      for (String connection : knownConnections) {
         RecordSampler recordSampler = recordSamplers.get(connection);
         long samples = recordSampler.getSampleSize();

         if (samples > 0) {
            Record sample = new Record(recordSampler);

            if (sampleRecorder != null) {
               sampleRecorder.record(connection, sample);
            }
         } else {
            Sample sample = new Blank();

            if (sampleRecorder != null) {
               sampleRecorder.record(connection, sample);
            }
         }
         recordSampler.reset();
      }
   }

   public void clear() {
      recordSamplers.clear();
   }

   private static class RecordSampler {

      private final SampleAverager packetWaitMillis;
      private final SampleAverager packetSize;
      private final String connection;

      public RecordSampler(String connection) {
         this.packetWaitMillis = new SampleAverager();
         this.packetSize = new SampleAverager();
         this.connection = connection;
      }

      public long getSampleSize() {
         return packetWaitMillis.count();
      }

      public long getAverageWaitMillis() {
         return packetWaitMillis.average();
      }

      public long getMaximumWaitMillis() {
         return packetWaitMillis.maximum();
      }

      public long getMinimumWaitMillis() {
         return packetWaitMillis.minimum();
      }

      public long getSumWait() {
         return packetWaitMillis.sum();
      }

      public long getAveragePacketSize() {
         return packetSize.average();
      }

      public long getMaximumPacketSize() {
         return packetSize.maximum();
      }

      public long getMinimumPacketSize() {
         return packetSize.minimum();
      }

      public long getSumPacketSize() {
         return packetSize.sum();
      }

      public void update(long latencyMillis, long size) {
         packetSize.sample(size);
         packetWaitMillis.sample(latencyMillis);
      }

      public void reset() {
         packetWaitMillis.reset();
         packetSize.reset();
      }
   }

   private static class Record implements Sample {

      private final long minimumWaitMillis;
      private final long maximumWaitMillis;
      private final long averageWaitMillis;
      private final long totalWaitMillis;
      private final long averageSize;
      private final long totalSize;
      private final long sampleTime;

      public Record(RecordSampler recordSampler) {
         this.averageWaitMillis = recordSampler.getAverageWaitMillis();
         this.minimumWaitMillis = recordSampler.getMinimumWaitMillis();
         this.maximumWaitMillis = recordSampler.getMaximumWaitMillis();
         this.totalWaitMillis = recordSampler.getSumWait();
         this.averageSize = recordSampler.getAveragePacketSize();
         this.totalSize = recordSampler.getSumPacketSize();
         this.sampleTime = System.currentTimeMillis();
      }

      @Override
      public long getSampleTime() {
         return sampleTime;
      }

      @Override
      public long getAverageWaitMillis() {
         return averageWaitMillis;
      }

      @Override
      public long getMinimumWaitMillis() {
         return minimumWaitMillis;
      }

      @Override
      public long getMaximumWaitMillis() {
         return maximumWaitMillis;
      }

      @Override
      public long getTotalWaitMillis() {
         return totalWaitMillis;
      }

      @Override
      public long getAverageSize() {
         return averageSize;
      }

      @Override
      public long getTotalSize() {
         return totalSize;
      }
   }

   private static class Blank implements Sample {

      private final long sampleTime;

      public Blank() {
         this.sampleTime = System.currentTimeMillis();
      }

      @Override
      public long getSampleTime() {
         return sampleTime;
      }

      @Override
      public long getMinimumWaitMillis() {
         return 0;
      }

      @Override
      public long getMaximumWaitMillis() {
         return 0;
      }

      @Override
      public long getAverageWaitMillis() {
         return 0;
      }

      @Override
      public long getTotalWaitMillis() {
         return 0;
      }

      @Override
      public long getAverageSize() {
         return 0;
      }

      @Override
      public long getTotalSize() {
         return 0;
      }
   }
}
