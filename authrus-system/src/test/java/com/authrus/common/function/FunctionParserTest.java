package com.authrus.common.function;

import com.authrus.common.function.FunctionParser;

import junit.framework.TestCase;

// someController.fun( x: 1, y: { "a", "b", "c", "d" } )

/*

FunctionParameter {

isQuoted()
isMap()
isList()

}


 */

public class FunctionParserTest extends TestCase {

   public void testFunction() {
      FunctionParser parser = new FunctionParser("fun(x: 1, y: 'Some text'  ,  o: \"something else\")");

      assertEquals(parser.getName(), "fun");
      assertEquals(parser.getParameters().get(0).getName(), "x");
      assertEquals(parser.getParameters().get(0).getValue(), "1");
      assertEquals(parser.getParameters().get(1).getName(), "y");
      assertEquals(parser.getParameters().get(1).getValue(), "Some text");
      assertEquals(parser.getParameters().get(2).getName(), "o");
      assertEquals(parser.getParameters().get(2).getValue(), "something else");
   }

   public void testIndexedArguments() {
      FunctionParser parser = new FunctionParser("fun(x: this.panel[\"some text 'quote'\"  ] .blah[ 'someIndex'],t:2)");

      assertEquals(parser.getName(), "fun");
      assertEquals(parser.getParameters().get(0).getName(), "x");
      assertEquals(parser.getParameters().get(0).getValue(), "this.panel[\"some text 'quote'\"].blah['someIndex']");
      assertFalse(parser.getParameters().get(0).isQuote());
      assertEquals(parser.getParameters().get(1).getName(), "t");
      assertEquals(parser.getParameters().get(1).getValue(), "2");
      assertFalse(parser.getParameters().get(1).isQuote());
   }

   public void testMessyFunction() {
      FunctionParser parser = new FunctionParser("fun (   x: 1, y  : 'Some text'  ,  o: \"something else\"  )");

      assertEquals(parser.getName(), "fun");
      assertEquals(parser.getParameters().get(0).getName(), "x");
      assertEquals(parser.getParameters().get(0).getValue(), "1");
      assertEquals(parser.getParameters().get(1).getName(), "y");
      assertEquals(parser.getParameters().get(1).getValue(), "Some text");
      assertEquals(parser.getParameters().get(2).getName(), "o");
      assertEquals(parser.getParameters().get(2).getValue(), "something else");
   }

   public void testQuotesInQuotes() {
      FunctionParser parser = new FunctionParser("callMethod (  text: \"some text with a 'quoted string' \" )");

      assertEquals(parser.getName(), "callMethod");
      assertEquals(parser.getParameters().get(0).getName(), "text");
      assertEquals(parser.getParameters().get(0).getValue(), "some text with a 'quoted string' ");
      assertTrue(parser.getParameters().get(0).isQuote());
   }


   public void testFunctionNavigation() {
      FunctionParser parser = new FunctionParser("someController.bean.fun (   x: 1, y  : 'Some text'  ,  o: \"something else\"  )");

      assertEquals(parser.getNavigation().length, 2);
      assertEquals(parser.getNavigation()[0], "someController");
      assertEquals(parser.getNavigation()[1], "bean");
      assertEquals(parser.getName(), "fun");
      assertEquals(parser.getParameters().get(0).getName(), "x");
      assertEquals(parser.getParameters().get(0).getValue(), "1");
      assertEquals(parser.getParameters().get(1).getName(), "y");
      assertEquals(parser.getParameters().get(1).getValue(), "Some text");
      assertTrue(parser.getParameters().get(1).isQuote());
      assertEquals(parser.getParameters().get(2).getName(), "o");
      assertEquals(parser.getParameters().get(2).getValue(), "something else");
   }
}
