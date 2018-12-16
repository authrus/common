package com.authrus.common.chart.reflect;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.authrus.common.chart.Axis;
import com.authrus.common.chart.Series;
import com.authrus.common.chart.reflect.PropertyChart;

public class PropertyChartTest {

   @SuppressWarnings("unused")
   private static class ExampleEvent {

      private final long time;
      private final int count;

      public ExampleEvent(long time, int count) {
         this.count = count;
         this.time = time;
      }

      public long getTime() {
         return time;
      }

      public int getCount() {
         return count;
      }
   }

   private static class ListSeries implements Series {

      private final List<Object> values;

      private ListSeries(List<Object> values) {
         this.values = values;
      }

      public int getLength() {
         return values.size();
      }

      public Object getValue(int index) {
         return values.get(index);
      }
   }

   @Test
   public void testPropertySeries() throws Exception {
      List<Object> exampleValues = new ArrayList<Object>();
      ListSeries listSeries = new ListSeries(exampleValues);
      PropertyChart propertyChart = new PropertyChart(listSeries, "Time v Count", "time", "count", ExampleEvent.class);

      exampleValues.add(new ExampleEvent(1, 10));
      exampleValues.add(new ExampleEvent(12, 11));
      exampleValues.add(new ExampleEvent(55, 12));
      exampleValues.add(new ExampleEvent(199, 13));
      exampleValues.add(new ExampleEvent(400, 14));
      exampleValues.add(new ExampleEvent(678, 15));

      assertEquals(propertyChart.getSeries(Axis.X).getValue(0), 1L);
      assertEquals(propertyChart.getSeries(Axis.X).getValue(1), 12L);
      assertEquals(propertyChart.getSeries(Axis.X).getValue(2), 55L);
      assertEquals(propertyChart.getSeries(Axis.X).getValue(3), 199L);
      assertEquals(propertyChart.getSeries(Axis.X).getValue(4), 400L);
      assertEquals(propertyChart.getSeries(Axis.X).getValue(5), 678L);

      assertEquals(propertyChart.getSeries(Axis.Y).getValue(0), 10);
      assertEquals(propertyChart.getSeries(Axis.Y).getValue(1), 11);
      assertEquals(propertyChart.getSeries(Axis.Y).getValue(2), 12);
      assertEquals(propertyChart.getSeries(Axis.Y).getValue(3), 13);
      assertEquals(propertyChart.getSeries(Axis.Y).getValue(4), 14);
      assertEquals(propertyChart.getSeries(Axis.Y).getValue(5), 15);
   }
}
