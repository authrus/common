package com.authrus.common.chart;

/**
 * Represents a line graph that can be plotted on an X Y axis. Each 
 * axis can provide a series of points to be plotted.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.common.chart.ChartSeries
 */
public interface Chart {
   String getName();
   String getName(Axis axis);
   Series getSeries(Axis axis);
   ChartType getType();
}
