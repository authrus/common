package com.authrus.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.authrus.attribute.SerializationStreamBuilder;

import junit.framework.TestCase;

public class SerializationStreamBuilderTest extends TestCase {

   public static class ExampleArrayObject implements Serializable {
      ArrayEntry[] array;
      List<ArrayEntry> list;
      public ExampleArrayObject(ArrayEntry[] array) {
         this.list = new ArrayList<ArrayEntry>(Arrays.asList(array));
         this.array = array;
      }
   }

   private static class ArrayEntry implements Serializable {
      String a;
      String b;
      public ArrayEntry(String a, String b){
         this.a = a;
         this.b = b;
      }
   }

   private static class SomethingWithAnExplicitVersion implements Serializable {
      private static final long serialVersionUID = 7037010394658684602L;
      String a;
      String b;
      public SomethingWithAnExplicitVersion(String a, String b){
         this.a = a;
         this.b = b;
      }
   }

   public void testSerializationStream() throws Exception {
      SerializationStreamBuilder builder = new SerializationStreamBuilder();
      ExampleArrayObject arrayObject = (ExampleArrayObject)builder.createInstance(ExampleArrayObject.class);

      assertNotNull(arrayObject);

      ArrayEntry arrayEntry = (ArrayEntry)builder.createInstance(ArrayEntry.class);

      assertNotNull(arrayEntry);

      SomethingWithAnExplicitVersion exampleVersion = (SomethingWithAnExplicitVersion)builder.createInstance(SomethingWithAnExplicitVersion.class);

      assertNotNull(exampleVersion);
    }
}

