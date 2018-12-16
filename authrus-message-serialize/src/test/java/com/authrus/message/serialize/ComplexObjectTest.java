package com.authrus.message.serialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.message.serialize.AttributeMarshaller;

import junit.framework.TestCase;

public class ComplexObjectTest extends TestCase {

   public static class ExampleObject implements Serializable {
      private String x = "This is X";
      int y = 0;
      ExampleChild child = new ExampleChild();
      Long nullLong;
      String nullString;
      ExampleChild nullChild;

      public ExampleObject() {
         super();
      }
   }

   public static class ExampleChild implements Serializable {
      Map<String, ExampleEntry> entry = new HashMap<String, ExampleEntry>();
      String[] blah = new String[] { "A", "B", "C" };

      public ExampleChild() {
         entry.put("10.0f", new ExampleEntry(10, null));
         entry.put("8.0f", new ExampleEntry(8));
         entry.put("3", new ExampleEntry(3, "Foo!"));
         entry.put("1145", new ExampleEntry(1145));
      }
   }

   public static class ExampleEntry implements Serializable {
      Set<String> set = new TreeSet<String>();
      List<Double> list = new LinkedList<Double>();
      String someString = "Blah!";
      long time = -1;
      Double nullD;

      public ExampleEntry(long value) {
         this(value, "Default!");
      }

      public ExampleEntry(long value, String text) {
         this.time = value;
         this.someString = text;
         set.add("sort");
         set.add("me");
         set.add("alphabetically");
         list.add(2.12d);
         list.add(11.3d);
      }
   }

   public void testSerialization() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      ExampleObject object = new ExampleObject();
      AttributeMarshaller marshaller = new AttributeMarshaller(serializer);
      Map<String, Object> message = marshaller.fromObject(object);
      System.err.println(new TreeMap<String, Object>(message).toString());
      ExampleObject recoveredObject = (ExampleObject) marshaller.toObject(message);

      assertEquals(recoveredObject.child.blah[0], object.child.blah[0]);
      assertEquals(recoveredObject.child.blah[1], object.child.blah[1]);
      assertEquals(recoveredObject.child.blah[2], object.child.blah[2]);
      assertNull(recoveredObject.nullString);
      assertNull(recoveredObject.nullChild);

   }

}
