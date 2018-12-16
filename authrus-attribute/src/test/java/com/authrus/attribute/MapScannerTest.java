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

public class MapScannerTest extends TestCase {

   private static final int ITERATIONS = 1000000;

   public static class ExampleMapObject implements Serializable {
      private final Map<String, String> values;
      private final String name;
      private final String value;

      public ExampleMapObject(Map<String, String> values, String name, String value) {
         this.values = values;
         this.name = name;
         this.value = value;
      }
   }

   public void testFieldScannerWrite() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      Map<String, String> exampleMap = new LinkedHashMap<String, String>();
      ExampleMapObject object = new ExampleMapObject(exampleMap, "n", "v");

      exampleMap.put("a", "A");
      exampleMap.put("b", "B");
      exampleMap.put("c", "C");

      long startTime = System.currentTimeMillis();

      for (int i = 0; i < ITERATIONS; i++) {
         Map<String, Object> values = new LinkedHashMap<String, Object>();
         AttributeWriter writer = new MapWriter(values);
         ClassResolver resolver = new ClassResolver();
         ObjectGraph graph = new ObjectGraph(scanner, resolver);
         ObjectNode node = graph.createNode(ExampleMapObject.class);
         Converter converter = node.getConverter(graph);

         converter.writeAttributes(writer, object, null, null);
      }
      long endTime = System.currentTimeMillis();

      System.err.println("TOTAL FOR " + ITERATIONS + ": " + (endTime - startTime) + " WHICH IS " + (ITERATIONS / ((endTime - startTime)) * 1000) + " PER SECOND");

      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);
      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleMapObject.class);
      Converter converter = node.getConverter(graph);

      converter.writeAttributes(writer, object, null, null);
      System.err.println(values);
   }

   public void testFieldScannerRead() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeReader reader = new MapReader(values);

      values.put("name", "n");
      values.put("value", "v");
      values.put("values.class", "java.util.LinkedHashMap");
      values.put("values{'a'}", "A");
      values.put("values{'b'}", "B");
      values.put("values{'c'}", "C");

      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleMapObject.class);
      Converter converter = node.getConverter(graph);
      ExampleMapObject instance = (ExampleMapObject) node.getInstance();

      assertNotNull(instance);

      converter.readAttributes(reader, instance, null, null);

      assertEquals(instance.name, "n");
      assertEquals(instance.value, "v");
      assertNotNull(instance.values);
      assertEquals(instance.values.get("a"), "A");
      assertEquals(instance.values.get("b"), "B");
      assertEquals(instance.values.get("c"), "C");

   }
}
