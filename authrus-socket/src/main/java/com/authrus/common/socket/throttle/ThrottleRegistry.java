package com.authrus.common.socket.throttle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.socket.HostAddress;

public class ThrottleRegistry {

   private final Map<String, ThrottleCapacity> overrides;
   private final Cache<String, ThrottleCapacity> cache;
   private final ThrottleCapacity primary;

   public ThrottleRegistry(ThrottleCapacity primary) {
      this(primary, Collections.EMPTY_MAP);
   }

   public ThrottleRegistry(ThrottleCapacity primary, Map<String, ThrottleCapacity> overrides) {
      this.cache = new LeastRecentlyUsedCache<String, ThrottleCapacity>();
      this.overrides = new LinkedHashMap<String, ThrottleCapacity>(overrides);
      this.primary = primary;
   }
   
   public synchronized Map<String, ThrottleCapacity> currentThrottles() {
      Map<String, ThrottleCapacity> throttles = new LinkedHashMap<String, ThrottleCapacity>(overrides);

      if(primary != null) {
         throttles.put(".*", primary);
      }
      return throttles;
   }

   public synchronized void applyThrottle(String addressPattern, long totalCapacity, int freePercentage, int alertPercentage) {
      if (alertPercentage > 100) {
         throw new IllegalStateException("Alert percentage " + alertPercentage + " cannot be greater than 100%");
      }
      if (freePercentage > 100) {
         throw new IllegalStateException("Free percentage " + freePercentage + " is greater than 100%");
      }
      ThrottleCapacity capacity = overrides.get(addressPattern);

      if (capacity == null) {
         createThrottle(addressPattern, totalCapacity, freePercentage, alertPercentage);
      } else {
         updateThrottle(addressPattern, totalCapacity, freePercentage, alertPercentage);
      }
   }

   private synchronized void updateThrottle(String addressPattern, long totalCapacity, int freePercentage, int alertPercentage) {
      ThrottleCapacity capacity = overrides.get(addressPattern);

      if (capacity != null) {
         capacity.setTotalBytes(totalCapacity);
         capacity.setFreePercentage(freePercentage);
         capacity.setAlertPercentage(alertPercentage);
      }
   }

   private synchronized void createThrottle(String addressPattern, long totalCapacity, int freePercentage, int alertPercentage) {
      ThrottleCapacity capacity = new ThrottleCapacity(totalCapacity, freePercentage, alertPercentage);

      overrides.put(addressPattern, capacity);
      cache.clear();
   }

   public synchronized void removeThrottle(String addressPattern) {
      overrides.remove(addressPattern);
      cache.clear();
   }

   public synchronized ThrottleCapacity resolveCapacity(String host) {
      ThrottleCapacity capacity = cache.fetch(host);

      if (capacity == null) {
         Set<String> patterns = overrides.keySet();

         for (String pattern : patterns) {
            capacity = overrides.get(pattern);

            if (host.equals(pattern) || host.matches(pattern)) {
               cache.cache(host, capacity);
               return capacity;
            }
         }
         cache.cache(host, primary);
         return primary;
      }
      return capacity;

   }

   public synchronized ThrottleCapacity resolveCapacity(HostAddress address) {
      String host = address.getHost();

      if (host != null) {
         return resolveCapacity(host);
      }
      return primary;
   }
}
