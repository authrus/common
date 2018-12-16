package com.authrus.common.util;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;

public class StopWatch {   

   private static final Logger LOG = LoggerFactory.getLogger(StopWatch.class);

   private final Cache<String, Long> startTimes;
   private final Cache<String, Long> details;
   private final String tag;
   private final boolean quiet;

   public StopWatch() {
      this("");
   }

   public StopWatch(String tag) {
      this(tag, false);
   }

   public StopWatch(String tag, boolean quiet) {
      this.startTimes = new LeastRecentlyUsedCache<String, Long>();
      this.details = new LeastRecentlyUsedCache<String, Long>();
      this.quiet = quiet;
      this.tag = tag;
   }

   public void before(String name) {
      long start = System.currentTimeMillis();

      if(startTimes.contains(name)) {
         throw new IllegalStateException("Time " + name + " has already started");
      }
      startTimes.cache(name, start);
   }

   public void after(String name) {
      long start = startTimes.take(name);
      long time = System.currentTimeMillis();
      long duration = time - start;

      if(!quiet) {
         LOG.info(tag + ": "  + name + " took " + duration + " ms");
      } else {
         details.cache(name, duration);
      }
   }

   public void dump() {
      Set<String> keySet = details.keySet();
      for(String key : keySet) {
         Long time = details.fetch(key);
         LOG.info(tag + ": "  + key + " took " + time + " ms");
      }
   }
}
