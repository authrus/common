package com.authrus.tuple.grid.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.SubscriptionConnection;

public class GridClient {

   public static class DebugListener implements TupleListener {

      public void onUpdate(Tuple message) {
         Map<String, Object> map = message.getAttributes();
         Set<String> keys = map.keySet();
         TreeSet<String> sortedKeys = new TreeSet<String>(keys);
         System.err.println();
         System.err.println("###############################");
         for (String key : sortedKeys) {
            Object value = map.get(key);
            String type = value == null ? "" : (" (" + value.getClass().getName() + ")"); 
            System.err.println(key + "=" + map.get(key) + type);
         }
      }

      public void onException(Exception cause) {
         cause.printStackTrace(System.err);
      }

      public void onHeartbeat() {
         System.err.println("heartbeat " + new Date());
      }

      public void onReset() {
         System.err.println("reset " + new Date());
      }
   }

   public static void main(String[] list) throws Exception {
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(monitor);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), list[0], Integer.parseInt(list[1]));
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor); 
      GridSubscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      DebugListener listener = new DebugListener();
      Origin origin = new Origin("test");
      Map<String, String> predicates = new HashMap<String, String>();
      for(int i = 2; i < list.length; i++) {
         predicates.put(list[i], "*");
      }
      Query query = new Query(origin, predicates);
      SubscriptionConnection connection = new SubscriptionConnection(subscriber, query);

      connection.register(listener);
      connection.connect();
      Thread.sleep(500000);
   }
}
