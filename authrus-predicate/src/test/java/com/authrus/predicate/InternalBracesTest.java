package com.authrus.predicate;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class InternalBracesTest extends TestCase {

   public void testInternalBraces() {
      PredicateParser parser = new PredicateParser("(blah.foo() == 'x') && foo == 'x'");
      ArgumentChecker checker = new ArgumentChecker("blah.foo()", "x", "foo");      
      parser.accept(checker);
   }
   
   private class ArgumentChecker implements Argument {
      private List<String> expectations;
      public ArgumentChecker(String... list){
         this.expectations = Arrays.asList(list);
      }
      public String getValue(String name){
         System.err.println(">>"+name+"<<");
         assertTrue(expectations.contains(name));
         return name;
      }
   }
   
}
