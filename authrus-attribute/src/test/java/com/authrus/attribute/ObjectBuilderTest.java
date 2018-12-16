package com.authrus.attribute;

import java.io.Serializable;

import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.SerializationBuilder;

import junit.framework.TestCase;

public class ObjectBuilderTest extends TestCase {

   public static class ExampleObject implements Serializable {
      private final String name;
      private final String value;

      public ExampleObject(String name, String value) {
         this.name = name;
         this.value = value;
      }
   }

   public void testInstantiator() throws Exception {
      ObjectBuilder instantiator = new SerializationBuilder();
      Object value = instantiator.createInstance(ExampleObject.class);
      assertNotNull(value);
   }
}
