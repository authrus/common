package com.authrus.common.reflect;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class PropertyLazyAccessorTest extends TestCase {


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
   
   public static class BagOTricks{
      
      private Map<Object, Object> values;
      
      public BagOTricks(Map<Object, Object> values) {
         this.values = values;
      }
      
      public Map<Object, Object> getValues(){
         return values;
      }
   }
   
   @Test
   public void testLongLazyPropertyAccess() throws Exception {
      PropertyPathAccessor propertyAccessor = new PropertyPathAccessor("values[other].value", BagOTricks.class);      
      ExampleValue exampleValue = new ExampleValue("some text");
      ExampleValue otherValue = new ExampleValue(12.34);
      Map<Object, Object> untypedMap = new LinkedHashMap<Object, Object>();
      BagOTricks tricks = new BagOTricks(untypedMap);
      
      untypedMap.put("example", exampleValue);
      untypedMap.put("other", otherValue);

      Object result = propertyAccessor.getValue(tricks);

      assertEquals(result, 12.34);
   }

   @Test
   public void testLazyPropertyAccess() throws Exception {
      PropertyLazyAccessor propertyAccessor = new PropertyLazyAccessor("value", Object.class);
      ExampleValue exampleValue = new ExampleValue("some text");
      ExampleValue otherValue = new ExampleValue(12.34);

      Object exampleResult = propertyAccessor.getValue(exampleValue);
      Object otherResult = propertyAccessor.getValue(otherValue);

      assertEquals(exampleResult, "some text");
      assertEquals(otherResult, 12.34);
   }
}
