package com.authrus.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import com.authrus.attribute.AttributeWriter;
import com.authrus.attribute.MapWriter;

import junit.framework.TestCase;

public class MapWriterTest extends TestCase {

   public void testKeyWriter() throws Exception {
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);

      writer.writeString("a", "A").writeFloat("f", 1.45f).writeSection("child.").writeString("a", "A").writeSection("{'blah'}.").writeString("x", "X");

      System.err.println(values);

      assertEquals(values.get("a"), "A");
      assertEquals(values.get("f"), 1.45f);
      assertEquals(values.get("child.a"), "A");
      assertEquals(values.get("child.{'blah'}.x"), "X");
   }

   public void testIndexWriter() throws Exception {
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);

      writer.writeString("a", "A").writeFloat("f", 1.45f).writeSection("child.").writeString("a", "A").writeSection("[1].").writeString("x", "X");

      System.err.println(values);

      assertEquals(values.get("a"), "A");
      assertEquals(values.get("f"), 1.45f);
      assertEquals(values.get("child.a"), "A");
      assertEquals(values.get("child.[1].x"), "X");
   }
}
