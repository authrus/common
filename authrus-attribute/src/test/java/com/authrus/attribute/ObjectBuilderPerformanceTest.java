package com.authrus.attribute;

import java.io.Serializable;

import com.authrus.attribute.SerializationBuilder;

import junit.framework.TestCase;

public class ObjectBuilderPerformanceTest extends TestCase {

   public static class Blah implements Serializable {
      String a;
      String b;
      String c;

      public Blah(String a, String b, String c) {
         this.a = a;
         this.b = b;
         this.c = c;
      }
   }

   public void testObjectBuilder() {
      SerializationBuilder builder = new SerializationBuilder();
      long start = System.currentTimeMillis();
      for (int i = 0; i < 1000000; i++) {
         builder.createInstance(Blah.class);
      }
      System.err.println("Time taken for 1 million is " + (System.currentTimeMillis() - start));
   }
}
