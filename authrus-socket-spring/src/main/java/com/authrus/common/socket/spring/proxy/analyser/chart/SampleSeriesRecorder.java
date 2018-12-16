package com.authrus.common.socket.spring.proxy.analyser.chart;

public class SampleSeriesRecorder implements SampleRecorder {

   private final SampleSeriesCache cache;

   public SampleSeriesRecorder(SampleSeriesCache cache) {
      this.cache = cache;
   }

   public void record(String name, Sample sample) {
      SampleSeries series = cache.getSeries(name);

      if (series != null) {
         series.sample(sample);
      }
   }
}
