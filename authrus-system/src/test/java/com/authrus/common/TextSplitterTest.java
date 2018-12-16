package com.authrus.common;

import com.authrus.common.util.StringSplitter;

import junit.framework.TestCase;

public class TextSplitterTest extends TestCase {

   public void testSplit() {
      String[] list = StringSplitter.splitBySpaces("Some text   with  spaces\nand stuff");

      assertEquals(list.length, 6);
   }

}
