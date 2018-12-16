package com.authrus.common.socket.spring.proxy.analyser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.socket.proxy.analyser.Packet;
import com.authrus.common.socket.proxy.analyser.PacketAnalyser;
import com.authrus.common.time.DateTime;
import com.authrus.common.time.SampleAverager;
import com.authrus.common.time.Time;

@ManagedResource(description = "Analyser for individual byte streams")
public class ConnectionAnalyser implements PacketAnalyser {

   private final Cache<String, ConnectionSampler> samplers;

   public ConnectionAnalyser() {
      this(100);
   }

   public ConnectionAnalyser(int capacity) {
      this.samplers = new LeastRecentlyUsedCache<String, ConnectionSampler>(capacity);
   }

   @ManagedOperation(description = "Shows the details for individual connections")
   public String showConnections() {
      MultiValueMap<DateTime, ConnectionSampler> recentUpdates = new LinkedMultiValueMap<DateTime, ConnectionSampler>();
      List<DateTime> updateTimes = new ArrayList<DateTime>();
      Set<DateTime> alreadyDone = new HashSet<DateTime>();
      StringBuilder builder = new StringBuilder();

      if (!samplers.isEmpty()) {
         Set<String> connections = samplers.keySet();

         builder.append("<table border='1'>");
         builder.append("<th>connection</th>");
         builder.append("<th>averageWait</th>");
         builder.append("<th>maximumWait</th>");
         builder.append("<th>minimumWait</th>");
         builder.append("<th>totalWait</th>");
         builder.append("<th>averageSize</th>");
         builder.append("<th>maximumSize</th>");
         builder.append("<th>minimumSize</th>");
         builder.append("<th>totalSize</th>");
         builder.append("<th>updateTime</th>");
         builder.append("<th>creationTime</th>");

         for (String connection : connections) {
            ConnectionSampler sampler = samplers.fetch(connection);
            DateTime updateTime = sampler.getUpdateTime();

            recentUpdates.add(updateTime, sampler);
            updateTimes.add(updateTime);
         }
         Collections.sort(updateTimes);
         Collections.reverse(updateTimes);

         for (DateTime updateTime : updateTimes) {
            if (alreadyDone.add(updateTime)) {
               List<ConnectionSampler> samplers = recentUpdates.get(updateTime);

               for (ConnectionSampler sampler : samplers) {
                  String connection = sampler.getConnection();
                  DateTime creationTime = sampler.getCreationTime();
                  long averageWait = sampler.getAverageWaitMillis();
                  long maximumWait = sampler.getMaximumWaitMillis();
                  long minimumWait = sampler.getMinimumWaitMillis();
                  long totalWait = sampler.getTotalWaitMillis();
                  long averageSize = sampler.getAverageSize();
                  long maximumSize = sampler.getMaximumSize();
                  long minimumSize = sampler.getMinimumSize();
                  long totalSize = sampler.getTotalSize();

                  builder.append("<tr>");
                  builder.append("<td>").append(connection).append("</td>");
                  builder.append("<td>").append(averageWait).append("</td>");
                  builder.append("<td>").append(maximumWait).append("</td>");
                  builder.append("<td>").append(minimumWait).append("</td>");
                  builder.append("<td>").append(totalWait).append("</td>");
                  builder.append("<td>").append(averageSize).append("</td>");
                  builder.append("<td>").append(maximumSize).append("</td>");
                  builder.append("<td>").append(minimumSize).append("</td>");
                  builder.append("<td>").append(totalSize).append("</td>");
                  builder.append("<td>").append(updateTime).append("</td>");
                  builder.append("<td>").append(creationTime).append("</td>");
                  builder.append("</tr>");
               }
            }
         }
         builder.append("</table>");
      }
      return builder.toString();
   }

   @ManagedOperation(description = "Clear all samplers")
   public void clear() {
      samplers.clear();
   }

   @Override
   public void analyse(Time startTime, Time endTime, Packet packetData) {
      String associatedConnection = packetData.connection();
      ConnectionSampler analyser = samplers.fetch(associatedConnection);

      if (analyser == null) {
         analyser = new ConnectionSampler(associatedConnection);
         samplers.cache(associatedConnection, analyser);
      }
      analyser.update(startTime, endTime, packetData);
   }

   private static class ConnectionSampler {

      private final AtomicReference<DateTime> updateTime;
      private final SampleAverager packetWaitMillis;
      private final SampleAverager packetSize;
      private final DateTime creationTime;
      private final String connection;

      public ConnectionSampler(String connection) {
         this.updateTime = new AtomicReference<DateTime>();
         this.packetWaitMillis = new SampleAverager();
         this.packetSize = new SampleAverager();
         this.creationTime = DateTime.now();
         this.connection = connection;
      }

      public String getConnection() {
         return connection;
      }

      public DateTime getCreationTime() {
         return creationTime;
      }

      public DateTime getUpdateTime() {
         DateTime timeStamp = updateTime.get();

         if (timeStamp == null) {
            return getCreationTime();
         }
         return timeStamp;
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

      public long getTotalWaitMillis() {
         return packetWaitMillis.sum();
      }

      public long getAverageSize() {
         return packetSize.average();
      }

      public long getMaximumSize() {
         return packetSize.maximum();
      }

      public long getMinimumSize() {
         return packetSize.minimum();
      }

      public long getTotalSize() {
         return packetSize.sum();
      }

      public void update(Time startTime, Time endTime, Packet packetData) {
         DateTime timeStamp = DateTime.now();
         long startTimeMillis = startTime.getMillisTime();
         long endTimeMillis = endTime.getMillisTime();
         int payloadSize = packetData.length();

         updateTime.set(timeStamp);
         packetSize.sample(payloadSize);
         packetWaitMillis.sample(endTimeMillis - startTimeMillis);
      }
   }
}
