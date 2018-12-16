package com.authrus.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

public class ArrayOfObjectsTest extends TestCase {

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

      public ArrayEntry(String a, String b) {
         this.a = a;
         this.b = b;
      }
   }

   public void testArrayOfObjects() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      ArrayEntry[] array = new ArrayEntry[5];
      array[2] = new ArrayEntry("2", "value");
      array[3] = new ArrayEntry("3", "otherValue");
      ExampleArrayObject object = new ExampleArrayObject(array);
      AttributeConverter marshaller = new AttributeConverter(serializer);
      Map<String, Object> message = marshaller.fromObject(object);
      System.err.println(new TreeMap<String, Object>(message).toString());
      ExampleArrayObject recoveredObject = (ExampleArrayObject) marshaller.toObject(message);

      assertNull(recoveredObject.array[0]);
      assertNull(recoveredObject.array[1]);
      assertNull(recoveredObject.array[4]);
      assertNotNull(recoveredObject.array[2]);
      assertNotNull(recoveredObject.array[3]);
      assertEquals(recoveredObject.array[2].a, object.array[2].a);
      assertEquals(recoveredObject.array[2].b, object.array[2].b);
      assertEquals(recoveredObject.array[3].a, object.array[3].a);
      assertEquals(recoveredObject.array[3].b, object.array[3].b);
   }

}
