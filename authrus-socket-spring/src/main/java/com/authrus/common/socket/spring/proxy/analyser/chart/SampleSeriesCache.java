package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;

@ManagedResource(description = "Cache of all samples collected")
public class SampleSeriesCache {

   private final Cache<String, SampleSeries> cache;
   private final AtomicInteger capacity;

   public SampleSeriesCache(int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, SampleSeries>();
      this.capacity = new AtomicInteger(capacity);
   }

   @ManagedAttribute(description = "Number of sample series collected")
   public int getCount() {
      return cache.size();
   }

   @ManagedAttribute(description = "Capacity of each series")
   public int getCapacity() {
      return capacity.get();
   }

   public Set<String> getNames() {
      return cache.keySet();
   }

   public SampleSeries getSeries(String name) {
      SampleSeries series = cache.fetch(name);
      int size = capacity.get();

      if (series == null) {
         series = new SampleSeries(size);
         cache.cache(name, series);
      }
      return series;
   }
}
