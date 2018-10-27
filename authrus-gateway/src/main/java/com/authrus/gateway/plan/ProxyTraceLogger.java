package com.authrus.gateway.plan;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.zuooh.common.time.DateTime;
import com.zuooh.http.proxy.trace.TraceCollector;
import com.zuooh.http.proxy.trace.TraceEvent;
import com.zuooh.http.proxy.trace.search.EventFormatter;

@Slf4j
public class ProxyTraceLogger implements TraceCollector {
   
   private final EventFormatter formatter;
   private final boolean enabled;
   
   public ProxyTraceLogger(boolean enabled) {
      this.formatter = new EventFormatter();
      this.enabled = enabled;
   }

   @Override
   public void collect(TraceEvent event) {
      if(enabled) {
         String thread = event.getThread();
         Object value = event.getValue();
         Object type = event.getEvent();
         String text = formatter.format(value);
         DateTime timeStamp = event.getDateTime();
         String time = timeStamp.formatDate("HH:mm:ss");
      
         if(!StringUtils.isBlank(text)) {
            log.info("{} [{}] {}: {}", time, thread, type, text);
         } else {
            log.info("{} [{}] {}", time, thread, type);
         }
      }
   }
}