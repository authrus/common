package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;

public class RowConverterTest extends TestCase {

   public void testConverter() throws Exception {
      Map<String, Object> data = new HashMap<String, Object>();

      data.put("a", "A");
      data.put("b", "B");
      data.put("c", "C");
      data.put("d", "D");
      data.put("e", "E");
      data.put("f", "F");

      String[] index = new String[] { "a", "b" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      RowConverter converter = new RowConverter(structure, schema, "type");
      Tuple tuple = new Tuple(data, "type");
      
      schema.getColumn("a");
      schema.getColumn("b");
      schema.getColumn("c");
      schema.getColumn("d");
      schema.getColumn("e");
      schema.getColumn("f");

      Row row = converter.toRow(tuple);

      assertEquals(row.getCell("a").getValue(), "A");
      assertEquals(row.getCell("b").getValue(), "B");
      assertEquals(row.getCell("c").getValue(), "C");
      assertEquals(row.getCell("d").getValue(), "D");
      assertEquals(row.getCell("e").getValue(), "E");
      assertEquals(row.getCell("f").getValue(), "F");

      assertEquals(schema.getColumn("a").getIndex(), 0);
      assertEquals(schema.getColumn("b").getIndex(), 1);
      assertEquals(schema.getColumn("c").getIndex(), 2);
      assertEquals(schema.getColumn("d").getIndex(), 3);
      assertEquals(schema.getColumn("e").getIndex(), 4);
      assertEquals(schema.getColumn("f").getIndex(), 5);

      Tuple recovered = converter.fromRow(row);
      Map<String, Object> attributes = recovered.getAttributes();

      assertEquals(attributes.get("a"), "A");
      assertEquals(attributes.get("b"), "B");
      assertEquals(attributes.get("c"), "C");
      assertEquals(attributes.get("d"), "D");
      assertEquals(attributes.get("e"), "E");
      assertEquals(attributes.get("f"), "F");
   }
}
