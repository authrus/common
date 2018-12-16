package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;

public class RowMergerTest extends TestCase {

   public void testMergeWithMissingCells() {
      String[] index = new String[] { "key" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      RowConverter converter = new RowConverter(structure, schema, "type");
      RowMerger merger = new RowMerger(structure, schema);

      schema.getColumn("key");
      schema.getColumn("bid");
      schema.getColumn("offer");
      schema.getColumn("product");
      schema.getColumn("volume");
      schema.getColumn("contact");

      Map<String, Object> message1 = new HashMap<String, Object>();
      Tuple tuple1 = new Tuple(message1, "type");
      
      message1.put("key", "123");
      message1.put("bid", "5.0");
      message1.put("offer", "4.2");
      message1.put("product", "AU3TB000123");
      message1.put("volume", "100000");
      message1.put("contact", "user.mail@ubs.com");

      Map<String, Object> message2 = new HashMap<String, Object>();
      Tuple tuple2 = new Tuple(message2, "type");

      message2.put("key", "123");
      message2.put("bid", "5.1");
      message2.put("offer", "4.2");
      message2.put("product", "AU3TB000123");
      message2.put("volume", "100000");

      Row row1 = converter.toRow(tuple1);
      Row row2 = converter.toRow(tuple2);

      assertEquals(row1.getCell("bid").getValue(), "5.0");
      assertEquals(row1.getCell("offer").getValue(), "4.2");
      assertEquals(row1.getCell("product").getValue(), "AU3TB000123");
      assertEquals(row1.getCell("volume").getValue(), "100000");
      assertEquals(row1.getCell("contact").getValue(), "user.mail@ubs.com");

      assertEquals(row2.getCell("bid").getValue(), "5.1");
      assertEquals(row2.getCell("offer").getValue(), "4.2");
      assertEquals(row2.getCell("product").getValue(), "AU3TB000123");
      assertEquals(row2.getCell("volume").getValue(), "100000");
      assertEquals(row2.getCell("contact"), null);

      Row merge = merger.merge(row2, row1);

      assertEquals(merge.getCell("bid").getValue(), "5.1");
      assertEquals(merge.getCell("offer").getValue(), "4.2");
      assertEquals(merge.getCell("product").getValue(), "AU3TB000123");
      assertEquals(merge.getCell("volume").getValue(), "100000");
      assertEquals(merge.getCell("contact").getValue(), null);
   }

   public void testRowMerger() {
      String[] index = new String[] { "key" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      RowConverter converter = new RowConverter(structure, schema, "type");
      RowMerger merger = new RowMerger(structure, schema);

      schema.getColumn("key");
      schema.getColumn("bid");
      schema.getColumn("offer");
      schema.getColumn("product");
      schema.getColumn("volume");
      schema.getColumn("contact");

      Map<String, Object> message1 = new HashMap<String, Object>();
      Tuple tuple1 = new Tuple(message1, "type");
      
      message1.put("key", "123");
      message1.put("bid", "5.0");
      message1.put("offer", "4.2");
      message1.put("product", "AU3TB000123");
      message1.put("volume", "100000");

      Map<String, Object> message2 = new HashMap<String, Object>();
      Tuple tuple2 = new Tuple(message2, "type");
      
      message2.put("key", "123");
      message2.put("bid", "5.1");
      message2.put("offer", "4.7");
      message2.put("product", "AU3TB000123");
      message2.put("volume", "100000");

      Map<String, Object> message3 = new HashMap<String, Object>();
      Tuple tuple3 = new Tuple(message3, "type");
      
      message3.put("key", "123");
      message3.put("bid", "4.9");
      message3.put("offer", "3.7");
      message3.put("product", "AU3TB000123");
      message3.put("volume", "100000");

      Row row1 = converter.toRow(tuple1);
      Row row2 = converter.toRow(tuple2);
      Row row3 = converter.toRow(tuple3);

      assertEquals(row1.getCell("bid").getValue(), "5.0");
      assertEquals(row1.getCell("offer").getValue(), "4.2");
      assertEquals(row1.getCell("product").getValue(), "AU3TB000123");
      assertEquals(row1.getCell("volume").getValue(), "100000");

      assertEquals(row2.getCell("bid").getValue(), "5.1");
      assertEquals(row2.getCell("offer").getValue(), "4.7");
      assertEquals(row2.getCell("product").getValue(), "AU3TB000123");
      assertEquals(row2.getCell("volume").getValue(), "100000");

      Row merge1 = merger.merge(row2, row1);

      assertEquals(merge1.getCell("bid").getValue(), "5.1");
      assertEquals(merge1.getCell("offer").getValue(), "4.7");
      assertEquals(merge1.getCell("product").getValue(), "AU3TB000123");
      assertEquals(merge1.getCell("volume").getValue(), "100000");

      Row merge2 = merger.merge(row3, merge1);

      assertEquals(merge2.getCell("bid").getValue(), "4.9");
      assertEquals(merge2.getCell("offer").getValue(), "3.7");
      assertEquals(merge2.getCell("product").getValue(), "AU3TB000123");
      assertEquals(merge2.getCell("volume").getValue(), "100000");
   }
}
