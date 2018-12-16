package com.authrus.attribute;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.authrus.attribute.AttributeReader;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.Converter;
import com.authrus.attribute.MapReader;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ObjectGraph;
import com.authrus.attribute.ObjectNode;
import com.authrus.attribute.ObjectScanner;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;

import junit.framework.TestCase;

public class CollectionScannerTest extends TestCase {

   public static class ExampleListObject implements Serializable {
      private List<String> values;
      private String name;
      private String value;

      public ExampleListObject(List<String> values, String name, String value) {
         this.values = values;
         this.name = name;
         this.value = value;
      }
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
      values.put("values.class", "java.util.ArrayList");
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
      assertEquals(instance.values.get(0), "A");
      assertEquals(instance.values.get(1), "B");
      assertEquals(instance.values.get(2), "C");

   }
}
