package com.authrus.common.chart;

import java.util.Date;

public class ObjectConverter {

   public Double toDouble(Object value) {
      Class type = value.getClass();
      Transform transform = transform(type);

      return transform.toDouble(value);
   }

   public Long toLong(Object value) {
      Class type = value.getClass();
      Transform transform = transform(type);

      return transform.toLong(value);
   }

   private Transform transform(Class type) {
      if (type == Double.class) {
         return new DoubleTransform();
      }
      if (type == String.class) {
         return new StringTransform();
      }
      if (type == Integer.class) {
         return new IntegerTransform();
      }
      if (type == Float.class) {
         return new FloatTransform();
      }
      if (type == Long.class) {
         return new LongTransform();
      }
      if (type == Date.class) {
         return new DateTransform();
      }
      if (type == int.class) {
         return new IntegerTransform();
      }
      if (type == float.class) {
         return new FloatTransform();
      }
      if (type == long.class) {
         return new LongTransform();
      }
      if (type == double.class) {
         return new DoubleTransform();
      }
      throw new IllegalArgumentException("Can not convert " + type);

   }

   public static interface Transform<T> {
      Double toDouble(T value);

      Long toLong(T value);
   }

   private static class StringTransform implements Transform<String> {

      @Override
      public Double toDouble(String value) {
         return Double.valueOf(value);
      }

      @Override
      public Long toLong(String value) {
         return Long.valueOf(value);
      }
   }

   private static class FloatTransform implements Transform<Float> {

      @Override
      public Double toDouble(Float value) {
         return Double.valueOf(value);
      }

      @Override
      public Long toLong(Float value) {
         return value.longValue();
      }
   }

   private static class LongTransform implements Transform<Long> {

      @Override
      public Double toDouble(Long value) {
         return Double.valueOf(value);
      }

      @Override
      public Long toLong(Long value) {
         return value;
      }
   }

   private static class IntegerTransform implements Transform<Integer> {

      @Override
      public Double toDouble(Integer value) {
         return Double.valueOf(value);
      }

      @Override
      public Long toLong(Integer value) {
         return Long.valueOf(value);
      }
   }

   private static class DateTransform implements Transform<Date> {

      @Override
      public Double toDouble(Date value) {
         return Double.valueOf(value.getTime());
      }

      @Override
      public Long toLong(Date value) {
         return value.getTime();
      }
   }

   private static class DoubleTransform implements Transform<Double> {

      @Override
      public Double toDouble(Double value) {
         return value;
      }

      @Override
      public Long toLong(Double value) {
         return value.longValue();
      }
   }
}
