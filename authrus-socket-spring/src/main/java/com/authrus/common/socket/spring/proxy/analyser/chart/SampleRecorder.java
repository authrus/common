package com.authrus.common.socket.spring.proxy.analyser.chart;

public interface SampleRecorder {
   void record(String connection, Sample sample);
}
