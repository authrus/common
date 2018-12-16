package com.authrus.common.chart.reflect;

import com.authrus.common.chart.Series;
import com.authrus.common.reflect.Accessor;
import com.authrus.common.reflect.PropertyAccessor;

public class PropertySeries implements Series {

   private final Accessor accessor;
   private final Series series;
   private final String name;

   public PropertySeries(Series series, String name, Class type) {
      this.accessor = new PropertyAccessor(name, type);
      this.series = series;
      this.name = name;
   }

   @Override
   public int getLength() {
      return series.getLength();
   }

   @Override
   public Object getValue(int index) {
      Object record = series.getValue(index);

      try {
         return accessor.getValue(record);
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   public String getName() {
      return name;
   }
}
