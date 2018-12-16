package com.authrus.tuple.query;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.predicate.Argument;
import com.authrus.predicate.PredicateParser;

public class PredicateParserTest extends TestCase {

   public void testAny() {
      PredicateParser parser = new PredicateParser("*");
      MockArgument message = new MockArgument();
      
      assertTrue(parser.accept(message)); 
   }
   
   public void testBooleanPredicate() {
      PredicateParser predicate = new PredicateParser("name == 'tom' && active == true");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((name == tom) && (active == true))");
      
      MockArgument message = new MockArgument();

      message.setAttribute("name", "tom");
      message.setAttribute("active", true);

      assertTrue(predicate.accept(message)); 
   }
   
   public void testLongAndIntegerAttributesPredicate() {
      PredicateParser predicate = new PredicateParser("productId == 10233 && timeStamp > 1234");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((productId == 10233) && (timeStamp > 1234))");
      
      MockArgument message = new MockArgument();

      message.setAttribute("productId", 10233);
      message.setAttribute("timeStamp", 13467L);
      message.setAttribute("company", "JPM");
      message.setAttribute("trader", "jim@bank.com");

      assertTrue(predicate.accept(message)); 
   }

   public void testStringWithQuotePredicate() {
      PredicateParser predicate = new PredicateParser("\"field{'name'}\" == 'test'");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(field{'name'} == test)");
      
      MockArgument message = new MockArgument();

      message.setAttribute("field{'name'}", "test");

      assertTrue(predicate.accept(message));
   }
   
   public void testAcceptAllPredicate() {
      PredicateParser predicate = new PredicateParser("*");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(*)");

      MockArgument message = new MockArgument();

      message.setAttribute("day", "Tuesday");

      assertTrue(predicate.accept(message));

      PredicateParser anyOrCondition = new PredicateParser("* || day == 'Tuesday'");

      assertEquals(anyOrCondition.toString(), "((*) || (day == Tuesday))");

      message.setAttribute("day", "Tuesday");

      assertTrue(anyOrCondition.accept(message));

      message.setAttribute("day", "Wednesday");

      assertTrue(anyOrCondition.accept(message));

      PredicateParser anAndCondition = new PredicateParser("* && day == 'Tuesday'");

      assertEquals(anAndCondition.toString(), "((*) && (day == Tuesday))");

      message.setAttribute("day", "Tuesday");

      assertTrue(anAndCondition.accept(message));

      message.setAttribute("day", "Wednesday");

      assertFalse(anAndCondition.accept(message));

   }

   public void testNotNull() {
      PredicateParser predicate = new PredicateParser("name == null");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(name == null)");

      MockArgument message = new MockArgument();

      message.setAttribute("day", "Tuesday");

      assertTrue(predicate.accept(message));

      message.setAttribute("name", "Tim");

      assertFalse(predicate.accept(message));
   }

   public void testStartWithNot() {
      PredicateParser predicate = new PredicateParser("!(name == 'Jim') && (day == Tuesday)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(!((name == Jim)) && (day == Tuesday))");

      MockArgument message = new MockArgument();

      message.setAttribute("name", "Tim");
      message.setAttribute("day", "Tuesday");

      assertTrue(predicate.accept(message));
   }

   public void testNotDeeplyEmbedded() {
      PredicateParser predicate = new PredicateParser("i < j && (!(i < 10))");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((i < j) && !((i < 10)))");

      MockArgument message = new MockArgument();

      message.setAttribute("i", "0");
      message.setAttribute("j", "2");

      assertFalse(predicate.accept(message));

      message.setAttribute("i", "10");

      assertTrue(predicate.accept(message));
   }

   public void testNot() {
      PredicateParser predicate = new PredicateParser("!(i < 10)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "!((i < 10))");

      MockArgument message = new MockArgument();

      message.setAttribute("i", "0");
      message.setAttribute("j", "2");

      assertFalse(predicate.accept(message));

      message.setAttribute("i", "10");

      assertTrue(predicate.accept(message));
   }

   public void testNegativeNumbers() throws Exception {
      PredicateParser predicate = new PredicateParser("(i < 10) && j >= -2");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((i < 10) && (j >= -2))");

      MockArgument message = new MockArgument();

      message.setAttribute("i", "2");
      message.setAttribute("j", "2");

      assertTrue(predicate.accept(message));

      message.setAttribute("j", "-2");

      assertTrue(predicate.accept(message));

      message.setAttribute("j", "-3");

      assertFalse("j is less than -2", predicate.accept(message));
   }

   public void testStringConditions() throws Exception {
      PredicateParser predicate = new PredicateParser("(name == 'Jim Beam' && taste == 'Good') || (beer < 2.50)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(((name == Jim Beam) && (taste == Good)) || (beer < 2.50))");

      MockArgument message = new MockArgument();

      message.setAttribute("name", "Jim Beam");
      message.setAttribute("taste", "Good");
      message.setAttribute("beer", "1.50");

      assertTrue("Jim Beam taste is good", predicate.accept(message));

      message.setAttribute("taste", "Bad");

      assertTrue("Beer is still less than 2.50", predicate.accept(message));

      message.setAttribute("beer", "2.50");

      assertFalse("Beer is too expensive", predicate.accept(message));

   }

   public void testMultipleConditions() throws Exception {
      PredicateParser predicate = new PredicateParser("(x == y && i == j) || (a == b)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(((x == y) && (i == j)) || (a == b))");
   }

   public void testSimplePredicate() throws Exception {
      PredicateParser predicate = new PredicateParser("(x == y) && (i == j)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((x == y) && (i == j))");
   }

   private static class MockArgument implements Argument {

      private final Map<String, Object> attributes;

      public MockArgument() {
         this.attributes = new HashMap<String, Object>();
      }

      public Object getValue(String name) {
         return attributes.get(name);
      }

      public void setAttribute(String name, Object value) {
         attributes.put(name, value);
      }
   }
}
