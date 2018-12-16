package com.authrus.common.chart.plot;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.authrus.common.chart.Axis;
import com.authrus.common.chart.ChartType;

public class PlotSeriesImage {

   private final RectangleInsets imageInsets;
   private final PlotSeries plotSeries;
   private final String title;
   private final int width;
   private final int height;

   public PlotSeriesImage(PlotSeries plotSeries, int width, int height) {
      this.imageInsets = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
      this.title = plotSeries.getTitle();
      this.plotSeries = plotSeries;
      this.height = height;
      this.width = width;
   }

   public BufferedImage createImage() {
      XYLineAndShapeRenderer renderer = createRenderer();
      JFreeChart lineChart = createChart();
      XYPlot lineGraph = lineChart.getXYPlot();
      ChartType type = plotSeries.getType();
      boolean showLine = type.isLine();
      boolean showDot = type.isDot();
      int index = 0;

      for (Plot plot : plotSeries) {
         if (plot != null) {
            renderer.setSeriesLinesVisible(index, showLine);
            renderer.setSeriesShapesVisible(index, showDot);
         }
         index++;
      }

      lineChart.setBackgroundPaint(Color.WHITE);
      lineGraph.setBackgroundPaint(Color.LIGHT_GRAY);
      lineGraph.setDomainGridlinePaint(Color.WHITE);
      lineGraph.setRangeGridlinePaint(Color.WHITE);
      lineGraph.setAxisOffset(imageInsets);
      lineGraph.setDomainCrosshairVisible(true);
      lineGraph.setRangeCrosshairVisible(true);
      lineGraph.setRenderer(renderer);

      return lineChart.createBufferedImage(width, height);
   }

   private XYLineAndShapeRenderer createRenderer() {
      return new XYLineAndShapeRenderer();
   }

   private JFreeChart createChart() {
      ChartType type = plotSeries.getType();

      if (type.isTime()) {
         return createTimeChart();
      }
      return createDefaultChart();
   }

   private JFreeChart createDefaultChart() {
      XYSeriesCollection graphData = new XYSeriesCollection();

      for (Plot plot : plotSeries) {
         XYSeries graphPoints = plot.getDefaultSeries();

         if (graphPoints != null) {
            graphData.addSeries(graphPoints);
         }
      }
      return createDefaultChart(graphData);
   }

   private JFreeChart createTimeChart() {
      TimeSeriesCollection graphData = new TimeSeriesCollection();

      for (Plot plot : plotSeries) {
         TimeSeries graphPoints = plot.getTimeSeries();

         if (graphPoints != null) {
            graphData.addSeries(graphPoints);
         }
      }
      return createTimeSeriesChart(graphData);
   }

   private JFreeChart createTimeSeriesChart(XYDataset data) {
      return ChartFactory.createTimeSeriesChart(title, plotSeries.getName(Axis.X), plotSeries.getName(Axis.Y), data, true, false, false);
   }

   private JFreeChart createDefaultChart(XYDataset data) {
      return ChartFactory.createXYLineChart(title, plotSeries.getName(Axis.X), plotSeries.getName(Axis.Y), data, PlotOrientation.VERTICAL, true, false, false);
   }
}
