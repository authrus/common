package com.authrus.tuple;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class TupleFilterTest extends TestCase {
   
   private static class TestTuplePublisher implements TuplePublisher {
      public final Set<String> types;
      public TestTuplePublisher(Set<String> types){
         this.types = types;
      }
      public Tuple publish(Tuple t){
         types.add(t.getType());
         return t;
      }
   }
   
   public void testSubscriptionMessageRouter() throws Exception {
      Set<String> types = new HashSet<String>();
      Set<String> patterns = new HashSet<String>();
      patterns.add("com.authrus.container.*");      
      patterns.add("com.authrus.message.*");
      TuplePublisher publisher = new TestTuplePublisher(types);
      TupleFilter allowMatches = new TupleFilter(publisher, patterns, true);
      TupleFilter denyMatches = new TupleFilter(publisher, patterns, false);
      
      denyMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerResponseEvent"));
      denyMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerRequestEvent"));
      denyMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerStatusEvent"));
      denyMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.message.NotificationMessage"));
      denyMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.example.ExampleEvent"));   
      
      assertEquals(types.size(), 1);
      assertEquals(types.iterator().next(), "com.authrus.example.ExampleEvent");
      
      types.clear();
      
      allowMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerResponseEvent"));
      allowMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerRequestEvent"));
      allowMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerStatusEvent"));
      allowMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.message.NotificationMessage"));
      allowMatches.publish(new Tuple(Collections.EMPTY_MAP, "com.authrus.example.ExampleEvent"));   
      
      assertEquals(types.size(), 4);
      
      assertTrue(types.contains("com.authrus.container.manager.console.ContainerResponseEvent"));
      assertTrue(types.contains("com.authrus.container.manager.console.ContainerRequestEvent"));
      assertTrue(types.contains("com.authrus.container.manager.console.ContainerStatusEvent"));
      assertTrue(types.contains("com.authrus.message.NotificationMessage"));
      assertFalse(types.contains("com.authrus.example.ExampleEvent"));
   }

}
