package com.authrus.common.chart.plot;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;

import com.authrus.common.chart.Axis;
import com.authrus.common.chart.Chart;
import com.authrus.common.chart.ChartType;
import com.authrus.common.chart.ObjectConverter;
import com.authrus.common.chart.Series;

public class Plot implements Chart {

   private final ObjectConverter converter;
   private final Chart chart;
   private final String name;

   public Plot(Chart chart) {
      this(chart, chart.getName());
   }

   public Plot(Chart chart, String name) {
      this.converter = new ObjectConverter();
      this.chart = chart;
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public ChartType getType() {
      return chart.getType();
   }

   @Override
   public String getName(Axis axis) {
      return chart.getName(axis);
   }

   @Override
   public Series getSeries(Axis axis) {
      return chart.getSeries(axis);
   }

   public TimeSeries getTimeSeries() {
      TimeSeries series = new TimeSeries(name);
      List<Long> x = getTimePlot(Axis.X);
      List<Double> y = getDoublePlot(Axis.Y);

      for (int i = 0; i < x.size(); i++) {
         if (x.get(i) != null && y.get(i) != null && x.get(i) > 0) {
            FixedMillisecond time = new FixedMillisecond(x.get(i));

            series.addOrUpdate(time, y.get(i));
         }
      }
      return series;
   }

   public XYSeries getDefaultSeries() {
      XYSeries series = new XYSeries(name);
      List<Double> x = getDoublePlot(Axis.X);
      List<Double> y = getDoublePlot(Axis.Y);

      for (int i = 0; i < x.size(); i++) {
         if (x.get(i) != null && y.get(i) != null) {
            series.addOrUpdate(x.get(i), y.get(i));
         }
      }
      return series;
   }

   public List<Long> getTimePlot(Axis axis) {
      LinkedList<Long> values = new LinkedList<Long>();
      Series series = chart.getSeries(axis);

      if (series != null) {
         int length = series.getLength();

         for (int i = 0; i < length; i++) {
            Object value = series.getValue(i);
            Long result = null;

            if (value != null) {
               result = converter.toLong(value);
            }
            values.add(result);
         }
      }
      return values;
   }

   public List<Double> getDoublePlot(Axis axis) {
      LinkedList<Double> values = new LinkedList<Double>();
      Series series = chart.getSeries(axis);

      if (series != null) {
         int length = series.getLength();

         for (int i = 0; i < length; i++) {
            Object value = series.getValue(i);
            Double result = null;

            if (value != null) {
               result = converter.toDouble(value);
            }
            values.add(result);
         }
      }
      return values;
   }
}
