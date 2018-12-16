package com.authrus.tuple.query;

import java.util.HashMap;
import java.util.Map;

import com.authrus.tuple.query.Query;
import com.authrus.tuple.query.QueryValidator;

import junit.framework.TestCase;

public class QueryValidatorTest extends TestCase {
   
   public void testQueryValidator() throws Exception {
      QueryValidator validator = new QueryValidator();
      Map<String, String> predicates = new HashMap<String, String>();
      Query query = new Query(null, predicates);
      
      predicates.put("a", "a == b");
      validator.validate(query);
      
      boolean failure = false;
      
      try {
         predicates.put("a", "*'"); // single quote not paired
         validator.validate(query);
      } catch(Exception e) {
         e.printStackTrace();
         failure = true;
      }
      assertTrue("Should fail because of parse error", failure);
      
   }

}
