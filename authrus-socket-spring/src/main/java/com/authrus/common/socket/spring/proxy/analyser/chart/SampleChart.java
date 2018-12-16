package com.authrus.common.socket.spring.proxy.analyser.chart;

import com.authrus.common.chart.Chart;
import com.authrus.common.chart.ChartType;
import com.authrus.common.chart.reflect.PropertyChart;

public class SampleChart {

   private final ChartType plot;
   private final String name;
   private final Class type;
   private final String x;
   private final String y;

   public SampleChart(Class type, ChartType plot, String name, String x, String y) {
      this.type = type;
      this.name = name;
      this.plot = plot;
      this.x = x;
      this.y = y;
   }

   public Chart getChart(SampleSeries series) {
      return new PropertyChart(series, name, x, y, type, plot);
   }

   public String getName() {
      return name;
   }

}
