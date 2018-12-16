package com.authrus.attribute;

import java.io.Serializable;
import java.util.HashMap;
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

public class ObjectTest extends TestCase {

   public static class ExampleObject implements Serializable {
      private ExampleInner inner;
      private String name;
      private String value;

      public ExampleObject(ExampleInner inner, String name, String value) {
         this.name = name;
         this.value = value;
         this.inner = inner;
      }
   }

   public static class ExampleInner implements Serializable {
      private Map<String, ExampleEntry> map;
      private int blah[];
      private String x;
      private String y;

      public ExampleInner(Map<String, ExampleEntry> map, int blah[], String x, String y) {
         this.map = map;
         this.blah = blah;
         this.x = x;
         this.y = y;
      }
   }

   public static class ExampleEntry implements Serializable {
      long id;

      public ExampleEntry(long id) {
         this.id = id;
      }
   }

   public void testWrite() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);
      Map<String, ExampleEntry> map = new HashMap<String, ExampleEntry>();
      map.put("jim", new ExampleEntry(3L));
      map.put("fred", new ExampleEntry(2L));
      ExampleInner inner = new ExampleInner(map, new int[] { 1, 2, 3 }, "X", "Y");
      ExampleObject object = new ExampleObject(inner, "n", "v");
      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleObject.class);
      Converter converter = node.getConverter(graph);

      converter.writeAttributes(writer, object, null, null);

      System.err.println(values);
      assertEquals(object.name, "n");
      assertEquals(object.value, "v");
   }

   public void testRead() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      ObjectScanner scanner = new ObjectScanner(factory);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeReader reader = new MapReader(values);

      values.put("name", "n");
      values.put("value", "v");
      values.put("inner.class", "com.authrus.attribute.ObjectTest$ExampleInner");
      values.put("inner.x", "X");
      values.put("inner.y", "Y");
      values.put("inner.blah.length", 0);
      values.put("inner.map.class", "java.util.HashMap");
      values.put("inner.blah{'A'}.id", 100L);
      values.put("inner.blah{'B'}.id", 200L);

      ClassResolver resolver = new ClassResolver();
      ObjectGraph graph = new ObjectGraph(scanner, resolver);
      ObjectNode node = graph.createNode(ExampleObject.class);
      Converter converter = node.getConverter(graph);
      ExampleObject instance = (ExampleObject) node.getInstance();

      assertNotNull(instance);

      converter.readAttributes(reader, instance, null, null);

      assertEquals(instance.name, "n");
      assertEquals(instance.value, "v");
   }
}
