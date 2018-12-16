package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;

public class NullUpdateTest extends TestCase {
   
   public void testNullDoesNotBumpVersion() throws Exception {
      Structure structure = new Structure(new String[]{"name"});   
      Schema schema = new ColumnAllocator(structure);   
      Table table = new Table(structure, schema);
      RowConverter converter = new RowConverter(structure, schema, "type");
      Map<String, Object> map1 = new HashMap<String, Object>();
      Map<String, Object> map2 = new HashMap<String, Object>();
      Map<String, Object> map3 = new HashMap<String, Object>();
      Tuple tuple1 = new Tuple(map1, "type");
      Tuple tuple2 = new Tuple(map2, "type");
      Tuple tuple3 = new Tuple(map3, "type");      
      
      map1.put("name", "a");
      map1.put("value", "b");

      map2.put("name", "a");
      map2.put("value", null);
      
      map3.put("name",  "a");
      map3.put("value", null);
      
      Row row1 = converter.toRow(tuple1);
      Row row2 = converter.toRow(tuple2);
      Row row3 = converter.toRow(tuple3);
      
      table.insertRow(row1);
      table.insertRow(row2);
      table.insertRow(row3);
   }

}
