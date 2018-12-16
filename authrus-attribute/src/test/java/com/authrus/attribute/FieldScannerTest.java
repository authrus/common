package com.authrus.attribute;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.authrus.attribute.AttributeReader;
import com.authrus.attribute.AttributeWriter;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.Converter;
import com.authrus.attribute.MapReader;
import com.authrus.attribute.MapWriter;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ObjectGraph;
import com.authrus.attribute.ObjectNode;
import com.authrus.attribute.ObjectScanner;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;

import junit.framework.TestCase;

public class FieldScannerTest extends TestCase {

   public static class ExampleObject implements Serializable {
      private ExampleSubObject sub;
      private String name;
      private String value;

      public ExampleObject(String name, String value, ExampleSubObject sub) {
         this.name = name;
         this.value = value;
         this.sub = sub;
      }
   }

   public static class ExampleSubObject implements Serializable {
      private String x;
      private float y;
      private double z;

      public ExampleSubObject(String x, float y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }

   public void testFieldScannerRead() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeReader reader = new MapReader(values);

      values.put("name", "a");
      values.put("value", "A");
      values.put("sub.class", "com.authrus.attribute.FieldScannerTest$ExampleSubObject");
      values.put("sub.x", "Z");
      values.put("sub.x", "Z");
      values.put("sub.y", 1.2f);
      values.put("sub.z", 22.3d);

      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleObject.class);
      Converter converter = node.getConverter(graph);
      ExampleObject instance = (ExampleObject) node.getInstance();

      assertNotNull(instance);

      converter.readAttributes(reader, instance, null, null);

      assertEquals(instance.name, "a");
      assertEquals(instance.value, "A");
      assertNotNull(instance.sub);
      assertEquals(instance.sub.x, "Z");
      assertEquals(instance.sub.y, 1.2f);
      assertEquals(instance.sub.z, 22.3d);

   }

   public void testFieldScanner() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      ExampleSubObject sub = new ExampleSubObject("Z", 1.2f, 22.3d);
      ExampleObject object = new ExampleObject("a", "A", sub);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);

      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleObject.class);

      assertNotNull(node);
      assertEquals(node.getType(), ExampleObject.class);
      assertNotNull(node.getInstance());

      Converter converter = node.getConverter(graph);

      assertNotNull(converter);

      converter.writeAttributes(writer, object, null, null);

      assertEquals(values.get("name"), "a");
      assertEquals(values.get("value"), "A");
      assertEquals(values.get("sub.class"), "com.authrus.attribute.FieldScannerTest$ExampleSubObject");
      assertEquals(values.get("sub.x"), "Z");
      assertEquals(values.get("sub.y"), 1.2f);
      assertEquals(values.get("sub.z"), 22.3d);
   }

   public void testPerformance() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      ExampleSubObject sub = new ExampleSubObject("Z", 1.2f, 22.3d);
      ExampleObject object = new ExampleObject("a", "A", sub);
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < 1000000; i++) {
         Map<String, Object> values = new LinkedHashMap<String, Object>();
         AttributeWriter writer = new MapWriter(values);
         ClassResolver resolver = new ClassResolver();
         ObjectGraph graph = new ObjectGraph(scanner, resolver);
         ObjectNode node = graph.createNode(ExampleObject.class);
         Converter converter = node.getConverter(graph);

         converter.writeAttributes(writer, object, null, null);
      }
      long endTime = System.currentTimeMillis();

      System.err.println("time taken to write 1 million records was " + (endTime - startTime));
   }

}
