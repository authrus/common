package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.LinkedList;
import java.util.List;

import com.authrus.common.chart.Chart;

public class SampleChartProvider {

   private final List<SampleChart> charts;

   public SampleChartProvider(List<SampleChart> charts) {
      this.charts = charts;
   }

   public List<Chart> getCharts(SampleSeries series) {
      List<Chart> list = new LinkedList<Chart>();

      for (SampleChart builder : charts) {
         Chart chart = builder.getChart(series);
         list.add(chart);
      }
      return list;
   }

}
