package com.authrus.common.chart.plot;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.authrus.common.chart.Axis;
import com.authrus.common.chart.Chart;

public class ImageBuilder {

   private final int width;
   private final int height;

   public ImageBuilder(int width, int height) {
      this.height = height;
      this.width = width;
   }

   public BufferedImage build(Chart chart, String title) {
      Plot plot = new Plot(chart, title);
      PlotImage plotImage = new PlotImage(plot, width, height);

      return plotImage.createImage();
   }

   public BufferedImage build(List<Chart> charts, String title) {
      List<Plot> plots = new LinkedList<Plot>();
      String x = null;
      String y = null;

      for (Chart chart : charts) {
         Plot plot = new Plot(chart);

         if (x == null) {
            x = plot.getName(Axis.X);
         }
         if (y == null) {
            y = plot.getName(Axis.Y);
         }
         plots.add(plot);
      }
      PlotSeries plot = new PlotSeries(plots, title, x, y);
      PlotSeriesImage plotImage = new PlotSeriesImage(plot, width, height);

      return plotImage.createImage();
   }
}
