package com.authrus.common.chart.reflect;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.authrus.common.chart.Series;
import com.authrus.common.chart.reflect.PropertySeries;

public class PropertySeriesTest {

   @SuppressWarnings("unused")
   private static class ExampleValue {

      private final Object value;

      public ExampleValue(Object value) {
         this.value = value;
      }

      public Object getValue() {
         return value;
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
      ExampleValue exampleValue = new ExampleValue("some text");
      ExampleValue otherValue = new ExampleValue(12.34);
      List<Object> exampleValues = new ArrayList<Object>();
      ListSeries listSeries = new ListSeries(exampleValues);
      PropertySeries propertySeries = new PropertySeries(listSeries, "value", ExampleValue.class);

      exampleValues.add(exampleValue);
      exampleValues.add(otherValue);

      Object first = propertySeries.getValue(0);
      Object second = propertySeries.getValue(1);

      assertEquals(first, "some text");
      assertEquals(second, 12.34);
   }
}
