package com.authrus.common.reflect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.authrus.common.reflect.PropertyAccessor;

public class PropertyAccessorTest {

   @SuppressWarnings("unused")
   private static class ExampleValue {

      private Object value;

      public ExampleValue(Object value) {
         this.value = value;
      }

      public Object getValue() {
         return value;
      }
   }

   @Test
   public void testPropertyAccess() throws Exception {
      PropertyAccessor propertyAccessor = new PropertyAccessor("value", ExampleValue.class);
      ExampleValue exampleValue = new ExampleValue("some text");
      ExampleValue otherValue = new ExampleValue(12.34);

      Object exampleResult = propertyAccessor.getValue(exampleValue);
      Object otherResult = propertyAccessor.getValue(otherValue);

      assertEquals(exampleResult, "some text");
      assertEquals(otherResult, 12.34);
   }
}
