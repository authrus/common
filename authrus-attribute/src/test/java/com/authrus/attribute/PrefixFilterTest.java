package com.authrus.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class PrefixFilterTest extends TestCase {
   
   public void testPrefix() throws Exception {
      Map<String, String> map = new HashMap<String, String>();      
      PrefixFilter filter = new PrefixFilter(map);
      
      map.put("blah.list[1]", "a");
      map.put("blah.list.name", "b");
      map.put("blah.list.name.class", "c");
      map.put("blah.list[2]", "d");
      map.put("blah.listen", "e");
      
      Iterator<String> keys = filter.readChildren("blah.list");      
      
      assertTrue(keys.hasNext());
      System.err.println(keys);     
   }
   
   public void testFindChildren() throws Exception {
      Map<String, String> map = new LinkedHashMap<String, String>();      
      PrefixFilter filter = new PrefixFilter(map);
      List<String> found = new ArrayList<String>();
      
      map.put("x.list[1]", "a");
      map.put("x.list.name", "b");
      map.put("x.list.name.class", "c");
      map.put("y.list[2]", "d");
      map.put("y.listen", "e");
      map.put("x.list.class", "dd");
      map.put("x.last.class", "yy");
      map.put("x.listing", "no-match");
      
      Iterator<String> keys = filter.readChildren("x.list");
      
      while(keys.hasNext()) {
         String value = keys.next();
         found.add(value);
      }      
      assertEquals(found.size(), 4);
      assertEquals(found.get(0), "[1]");
      assertEquals(found.get(1), "name");
      assertEquals(found.get(2), "name.class");
      assertEquals(found.get(3), "class");
   }

}
