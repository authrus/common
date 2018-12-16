package com.authrus.predicate;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class PredicateParserTest extends TestCase {
   
   public void testVariable() {
      PredicateParser parser = new PredicateParser("variable");
      
      assertEquals(parser.toString(), "(variable)");
   }

   public void testSimpleExpression() {
      PredicateParser parser = new PredicateParser("flag == true");
      
      assertEquals(parser.toString(), "(flag == true)");
   }
   
   public void testAcceptAllPredicate() {
      PredicateParser predicate = new PredicateParser("*");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(*)");

      MockMessage message = new MockMessage();

      message.setValue("day", "Tuesday");

      assertTrue(predicate.accept(message));

      PredicateParser anyOrCondition = new PredicateParser("* || day == 'Tuesday'");

      assertEquals(anyOrCondition.toString(), "((*) || (day == Tuesday))");

      message.setValue("day", "Tuesday");

      assertTrue(anyOrCondition.accept(message));

      message.setValue("day", "Wednesday");

      assertTrue(anyOrCondition.accept(message));

      PredicateParser anAndCondition = new PredicateParser("* && day == 'Tuesday'");

      assertEquals(anAndCondition.toString(), "((*) && (day == Tuesday))");

      message.setValue("day", "Tuesday");

      assertTrue(anAndCondition.accept(message));

      message.setValue("day", "Wednesday");

      assertFalse(anAndCondition.accept(message));

   }

   public void testNotNull() {
      PredicateParser predicate = new PredicateParser("name == null");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(name == null)");

      MockMessage message = new MockMessage();

      message.setValue("day", "Tuesday");

      assertTrue(predicate.accept(message));

      message.setValue("name", "Tim");

      assertFalse(predicate.accept(message));
   }

   public void testStartWithNot() {
      PredicateParser predicate = new PredicateParser("!(name == 'Jim') && (day == 'Tuesday')");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(!((name == Jim)) && (day == Tuesday))");

      MockMessage message = new MockMessage();

      message.setValue("name", "Tim");
      message.setValue("day", "Tuesday");

      assertTrue(predicate.accept(message));
   }

   public void testNotDeeplyEmbedded() {
      PredicateParser predicate = new PredicateParser("i < j && (!(i < 10))");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((i < j) && !((i < 10)))");

      MockMessage message = new MockMessage();

      message.setValue("i", "0");
      message.setValue("j", "2");

      assertFalse(predicate.accept(message));

      message.setValue("i", "10");

      assertTrue(predicate.accept(message));
   }

   public void testNot() {
      PredicateParser predicate = new PredicateParser("!(i < 10)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "!((i < 10))");

      MockMessage message = new MockMessage();

      message.setValue("i", "0");
      message.setValue("j", "2");

      assertFalse(predicate.accept(message));

      message.setValue("i", "10");

      assertTrue(predicate.accept(message));
   }

   public void testNegativeNumbers() throws Exception {
      PredicateParser predicate = new PredicateParser("(i < 10) && j >= -2");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "((i < 10) && (j >= -2))");

      MockMessage message = new MockMessage();

      message.setValue("i", "2");
      message.setValue("j", "2");

      assertTrue(predicate.accept(message));

      message.setValue("j", "-2");

      assertTrue(predicate.accept(message));

      message.setValue("j", "-3");

      assertFalse("j is less than -2", predicate.accept(message));
   }

   public void testStringConditions() throws Exception {
      PredicateParser predicate = new PredicateParser("(name == 'Jim Beam' && taste == 'Good') || (beer < 2.50)");

      assertNotNull(predicate);
      assertEquals(predicate.toString(), "(((name == Jim Beam) && (taste == Good)) || (beer < 2.50))");

      MockMessage message = new MockMessage();

      message.setValue("name", "Jim Beam");
      message.setValue("taste", "Good");
      message.setValue("beer", "1.50");

      assertTrue("Jim Beam taste is good", predicate.accept(message));

      message.setValue("taste", "Bad");

      assertTrue("Beer is still less than 2.50", predicate.accept(message));

      message.setValue("beer", "2.50");

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

   private static class MockMessage implements Argument {

      private final Map<String, String> attributes;

      public MockMessage() {
         this.attributes = new HashMap<String, String>();
      }
      public void setValue(String name, String value) {
         attributes.put(name, value);
      }

      @Override
      public String getValue(String name) {
         return attributes.get(name);
      }
   }
}
