package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.authrus.common.chart.Chart;
import com.authrus.common.chart.plot.Plotter;

public class ChartSeriesPlotter {

   private final SampleChartProvider provider;
   private final SampleSeriesCache cache;
   private final Plotter plotter;
   private final String title;

   public ChartSeriesPlotter(SampleChartProvider provider, SampleSeriesCache cache, String title, int width, int height) {
      this.plotter = new Plotter(width, height);
      this.provider = provider;
      this.title = title;
      this.cache = cache;
   }

   public int getCount() {
      return cache.getCount();
   }

   public Set<String> getNames() {
      return cache.getNames();
   }

   public byte[] getPlot(String name) throws IOException {
      SampleSeries series = cache.getSeries(name);
      List<Chart> charts = provider.getCharts(series);
      String header = String.format("%s (%s)", title, name);

      return plotter.plot(charts, header);
   }
}
