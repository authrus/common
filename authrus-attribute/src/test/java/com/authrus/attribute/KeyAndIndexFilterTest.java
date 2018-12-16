package com.authrus.attribute;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

public class KeyAndIndexFilterTest extends TestCase {

   public void testKeyReader() throws Exception {
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeReader reader = new MapReader(values);

      values.put("name", "n");
      values.put("value", "v");
      values.put("values{'a'}.x", "A");
      values.put("values{'b'}.x.y", "B");
      values.put("values{'c'}", "C");

      Iterator<String> keys = reader.readKeys("values");

      assertTrue(keys.hasNext());
      assertEquals(keys.next(), "a");
      assertTrue(keys.hasNext());
      assertEquals(keys.next(), "b");
      assertTrue(keys.hasNext());
      assertEquals(keys.next(), "c");
   }

   public void testIndexReader() throws Exception {
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeReader reader = new MapReader(values);

      values.put("name", "n");
      values.put("value", "v");
      values.put("values[0].x", "A");
      values.put("values[2].x.y", "B");
      values.put("values[14]", "C");

      Iterator<Integer> indexes = reader.readIndexes("values");

      assertTrue(indexes.hasNext());
      assertEquals(indexes.next(), new Integer(0));
      assertTrue(indexes.hasNext());
      assertEquals(indexes.next(), new Integer(2));
      assertTrue(indexes.hasNext());
      assertEquals(indexes.next(), new Integer(14));
   }
}
