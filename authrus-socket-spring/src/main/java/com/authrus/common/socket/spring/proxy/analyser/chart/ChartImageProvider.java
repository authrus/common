package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ChartImageProvider {

   private final Map<String, ChartSeriesPlotter> plotters;

   public ChartImageProvider(Map<String, ChartSeriesPlotter> plotters) {
      this.plotters = plotters;
   }

   public Set<String> getCharts() {
      return plotters.keySet();
   }

   public Set<String> getSeries(String chart) {
      ChartSeriesPlotter plotter = plotters.get(chart);

      if (plotter != null) {
         return plotter.getNames();
      }
      return Collections.emptySet();
   }

   public ChartImage getImage(String chart, String series) {
      ChartSeriesPlotter plotter = plotters.get(chart);

      if (plotter != null) {
         return new ChartImage(plotter, series, "image/png");
      }
      return null;
   }
}
