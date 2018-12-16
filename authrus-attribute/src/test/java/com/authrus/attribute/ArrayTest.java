package com.authrus.attribute;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class ArrayTest extends TestCase {

   public static class ExampleListObject implements Serializable {
      private String[] values;
      private String name;
      private String value;

      public ExampleListObject(String[] values, String name, String value) {
         this.values = values;
         this.name = name;
         this.value = value;
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

      values.put("name", "n");
      values.put("value", "v");
      values.put("values.length", 3);
      values.put("values[0]", "A");
      values.put("values[1]", "B");
      values.put("values[2]", "C");

      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleListObject.class);
      Converter converter = node.getConverter(graph);
      ExampleListObject instance = (ExampleListObject) node.getInstance();

      assertNotNull(instance);

      converter.readAttributes(reader, instance, null, null);

      assertEquals(instance.name, "n");
      assertEquals(instance.value, "v");
      assertNotNull(instance.values);
      assertNotNull(instance.values.length);
      assertEquals(instance.values[0], "A");
      assertEquals(instance.values[1], "B");
      assertEquals(instance.values[2], "C");

   }
}
