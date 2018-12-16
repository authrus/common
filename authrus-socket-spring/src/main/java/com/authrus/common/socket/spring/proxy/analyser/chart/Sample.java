package com.authrus.common.socket.spring.proxy.analyser.chart;

public interface Sample {
   long getSampleTime();
   long getMinimumWaitMillis();
   long getMaximumWaitMillis();
   long getAverageWaitMillis();
   long getTotalWaitMillis();
   long getAverageSize();
   long getTotalSize();
}
