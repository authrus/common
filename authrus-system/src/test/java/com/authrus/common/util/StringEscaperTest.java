package com.authrus.common.util;

import com.authrus.common.util.StringEscaper;

import junit.framework.TestCase;

public class StringEscaperTest extends TestCase {
   
   public void testString() throws Exception {
      StringEscaper escaper = new StringEscaper();
      assertEquals(escaper.decode("red\u00A2ent"), "red?ent");
   }

}
