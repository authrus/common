package com.authrus.message.invoke;

import junit.framework.TestCase;

import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;

public class InvocationBrokerTest extends TestCase {

   public void testInvocationBrokerSuccess() {
      final InvocationBroker broker = new InvocationBroker(10000, 10000);
      final Invocation invocation = new Invocation(String.class, "java.lang.String.toString()", null);
      final Origin origin = new Origin("test");
      final Query query = new Query(origin);

      broker.subscribe("host:123", query);
      broker.register("X", invocation);

      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(1000);
               ReturnValue value = new ReturnValue(ReturnStatus.SUCCESS, "some string");
               broker.notify("X", value);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
      thread.start();

      ReturnValue value = broker.wait("X");

      assertEquals(value.getValue(), "some string");
      assertEquals(value.getStatus(), ReturnStatus.SUCCESS);
   }

   public void testInvocationBrokerTimeout() {
      InvocationBroker broker = new InvocationBroker(2000, 10000);
      Invocation invocation = new Invocation(String.class, "java.lang.String.toString()", null);
      Origin origin = new Origin("test");
      Query query = new Query(origin);

      broker.subscribe("host:123", query);
      broker.register("X", invocation);

      ReturnValue value = broker.wait("X");

      assertEquals(value.getStatus(), ReturnStatus.TIMEOUT);
   }
}
