package com.authrus.tuple.grid;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.tuple.query.PredicateFilter;

public class PredicateFilterTest extends TestCase{
   public void testPredicateFilter() throws Exception{
      Map<String, String> expr=new LinkedHashMap<String, String>();
      expr.put("com.x.*", "*");
      expr.put("com.y.*", "a != 'A'");
      expr.put("com.z.test", "*");
      expr.put("com.z.*", "t !='z'");
      PredicateFilter filter = new PredicateFilter(expr);
      assertNull(filter.getPredicate("blah"));
      assertNull(filter.getPredicate("blah"));      
      assertEquals(filter.getPredicate("com.x.Blah").toString(), "(*)");
      assertEquals(filter.getPredicate("com.x.X").toString(), "(*)");
      assertEquals(filter.getPredicate("com.z.test").toString(), "(*)");
      assertEquals(filter.getPredicate("com.z.tester").toString(), "(t != z)");        
   }

}
