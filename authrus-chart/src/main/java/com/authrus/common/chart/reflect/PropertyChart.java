package com.authrus.common.chart.reflect;

import com.authrus.common.chart.Axis;
import com.authrus.common.chart.Chart;
import com.authrus.common.chart.ChartType;
import com.authrus.common.chart.Series;

public class PropertyChart implements Chart {

   private final PropertySeries x;
   private final PropertySeries y;
   private final ChartType type;
   private final String name;

   public PropertyChart(Series series, String name, String x, String y, Class type) {
      this(series, name, x, y, type, ChartType.LINE);
   }

   public PropertyChart(Series series, String name, String x, String y, Class type, ChartType chart) {
      this.x = new PropertySeries(series, x, type);
      this.y = new PropertySeries(series, y, type);
      this.type = chart;
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getName(Axis axis) {
      if (axis == Axis.X) {
         return x.getName();
      }
      return y.getName();
   }

   @Override
   public Series getSeries(Axis axis) {
      if (axis == Axis.X) {
         return x;
      }
      return y;
   }

   @Override
   public ChartType getType() {
      return type;
   }
}
