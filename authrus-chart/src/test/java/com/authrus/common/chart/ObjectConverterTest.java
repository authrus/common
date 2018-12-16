package com.authrus.common.chart;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ObjectConverterTest {

   @Test
   public void testConversion() {
      ObjectConverter converter = new ObjectConverter();

      assertEquals(new Double(12.5), converter.toDouble("12.5"));
      assertEquals(new Double(12.0), converter.toDouble(12));
      assertEquals(new Double(3.0), converter.toDouble(3L));
      assertEquals(new Double(7.0), converter.toDouble(7));

      assertEquals(new Long(12), converter.toLong("12"));
      assertEquals(new Long(12), converter.toLong(12));
      assertEquals(new Long(3), converter.toLong(3L));
      assertEquals(new Long(7), converter.toLong(7));
   }
}
