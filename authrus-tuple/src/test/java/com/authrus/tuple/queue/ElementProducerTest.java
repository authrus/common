package com.authrus.tuple.queue;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.io.DataDispatcher;
import com.authrus.tuple.query.PredicateFilter;

public class ElementProducerTest extends TestCase {

   public void testElementProducerPerformance() {
      Map<String, String> predicates = new HashMap<String, String>();
      PredicateFilter filter = new PredicateFilter(predicates);

      predicates.put(MockElement.class.getName(), "*");

      DataDispatcher dispatcher = new MockDispatcher();
      ElementProducer producer = new ElementProducer(dispatcher, filter);
      Map<String, Object> attributes = new HashMap<String, Object>();

      attributes.put("a", "A");
      attributes.put("b", "B");
      attributes.put("c", "C");
      attributes.put("d", "D");
      attributes.put("e", "E");
      attributes.put("f", "F");

      Element element = new Element(attributes, MockElement.class.getName());
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < 1000000; i++) {
         ElementBatch batch = new ElementBatch();
         batch.insert(element);
         producer.onInsert(batch, MockElement.class.getName());
      }
      long endTime = System.currentTimeMillis();

      System.err.println("Time taken to produce 1 millions messages was " + (endTime - startTime));
   }

   public static class MockElement {

   }

   public static class MockDispatcher implements DataDispatcher {

      @Override
      public void dispatch(ByteBuffer buffer) {}      

   }

}
