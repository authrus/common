package com.authrus.common.socket.spring.throttle;

import java.util.Map;
import java.util.Set;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.socket.throttle.ThrottleCapacity;
import com.authrus.common.socket.throttle.ThrottleRegistry;

@ManagedResource(description="Registry that maintains throttle data")
public class ThrottleRegistryManager {

   private final ThrottleRegistry registry;

   public ThrottleRegistryManager(ThrottleRegistry registry) {
      this.registry = registry;
   }

   @ManagedOperation(description="Show the capacity settings")
   public synchronized String showThrottles() {
      Map<String, ThrottleCapacity> throttles = registry.currentThrottles();
      
      if(!throttles.isEmpty()) {
         StringBuilder builder = new StringBuilder();
   
         builder.append("<table border='1'>");
         builder.append("<th>addressPattern</th>");
         builder.append("<th>totalCapacity</th>");
         builder.append("<th>freeCapacity</th>");
         builder.append("<th>alertCapacity</th>");
         builder.append("<th>freePercentage</th>");
         builder.append("<th>alertPercentage</th>");
   
         Set<String> patterns = throttles.keySet();
   
         for (String pattern : patterns) {
            ThrottleCapacity capacity = throttles.get(pattern);
   
            if (capacity != null) {
               double alertPercent = capacity.getAlertPercentage();
               double freePercent = capacity.getFreePercentage();
               long totalBytes = capacity.getTotalBytes();
               long freeBytes = Math.round(totalBytes * freePercent * 0.01);
               long alertBytes = Math.round(totalBytes * alertPercent * 0.01);
   
               builder.append("<tr>");
               builder.append("<td>").append(pattern).append("</td>");
               builder.append("<td>").append(totalBytes).append("</td>");
               builder.append("<td>").append(freeBytes).append("</td>");
               builder.append("<td>").append(alertBytes).append("</td>");
               builder.append("<td>").append(freePercent).append("%</td>");
               builder.append("<td>").append(alertPercent).append("%</td>");
               builder.append("</tr>");
            }
         }
         builder.append("</table>");
         return builder.toString();
      }
      return null;
   }

   @ManagedOperation(description="Set a throttle for an I.P address pattern")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="addressPattern", description="I.P address pattern"),
      @ManagedOperationParameter(name="totalCapacity", description="Total capacity in bytes per time unit"),
      @ManagedOperationParameter(name="freePercentage", description="Percentage of total capacity that is free"),
      @ManagedOperationParameter(name="alertPercentage", description="Percentage of total capacity used for an alert") 
   })
   public synchronized void applyThrottle(String addressPattern, long totalCapacity, int freePercentage, int alertPercentage) {
      registry.applyThrottle(addressPattern, totalCapacity, freePercentage, alertPercentage);
   }

   @ManagedOperation(description="Remove a throttle for an I.P address pattern")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="addressPattern", description="I.P address pattern") 
   })
   public synchronized void removeThrottle(String addressPattern) {
      registry.removeThrottle(addressPattern);
   }
}
