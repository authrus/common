package com.authrus.tuple.subscribe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class SubscriptionMessageRouterTest extends TestCase {
   
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
      TuplePublisher publisher = new TestTuplePublisher(types);
      Set<String> deny = new HashSet<String>();
      SubscriptionMessageRouter router = new SubscriptionMessageRouter(publisher, deny);
      deny.add("y");      
      deny.add("z");
      router.onUpdate("y", new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerResponseEvent"));
      router.onUpdate("y", new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerRequestEvent"));
      router.onUpdate("y", new Tuple(Collections.EMPTY_MAP, "com.authrus.container.manager.console.ContainerStatusEvent"));
      router.onUpdate("z", new Tuple(Collections.EMPTY_MAP, "com.authrus.message.NotificationMessage"));
      router.onUpdate("x", new Tuple(Collections.EMPTY_MAP, "com.authrus.example.ExampleEvent"));   
      
      assertEquals(types.size(), 1);
      assertEquals(types.iterator().next(), "com.authrus.example.ExampleEvent");      
   }

}
