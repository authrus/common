package com.authrus.tuple.tunnel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.TransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.transport.trace.TraceLogger;
import com.authrus.transport.tunnel.TunnelBuilder;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class TunnelSubscriptionTest extends TestCase {
   
   private static final int GRID_PORT = 23342;
   private static final long EXPIRY = 5000;
   private static final int HTTP_TUNNEL_PROXY_PORT = 80;
   
   public TransportBuilder createBuilder(boolean tunnel) throws Exception {
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      TraceLogger socketLogger = new TraceLogger(); 
      TraceAgent analyzer = new TraceAgent(socketLogger);
      
      if(tunnel) {
         DirectSocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", HTTP_TUNNEL_PROXY_PORT);
         TunnelBuilder tunnelBuilder = new TunnelBuilder(builder, reactor, "localhost:" + GRID_PORT, "/");    

         return new DirectTransportBuilder(tunnelBuilder, reactor);
      }
      DirectSocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", GRID_PORT);

      return new DirectTransportBuilder(builder, reactor);
   }
 
   public void testBootstrapOnSubscribe() throws Exception {
      ThreadPool pool = new ThreadPool(10);
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);      
      TransportBuilder transportBuilder = createBuilder(true);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener listener = new SessionRegistryListener(monitor);
      Subscriber subscriber = new GridSubscriber(listener, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, listener, GRID_PORT);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessageUpdateListener updateListener = new MessageUpdateListener();
      
      grids.put("message", grid);
      predicates.put("message", "*");
      
      server.start();      
      subscriber.subscribe(updateListener, query);
      
      for(int i = 0; i < 1000; i++) {
         int index = i % 5000;
         Tuple message1 = createMessage("message"+index, "name"+index, "value"+i, System.currentTimeMillis());         
         publisher.publish(message1);
         Thread.sleep(1);
      }
      Thread.sleep(1000);
   }   
   
   public Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   public static class MessageUpdateListener implements TupleListener {
      
      private final Map<String, Map<String, Object>> messages;
      private final Map<String, AtomicInteger> counts;
      private final AtomicLong updates;
      
      public MessageUpdateListener() {
         this.messages = new ConcurrentHashMap<String, Map<String, Object>>();
         this.counts = new ConcurrentHashMap<String, AtomicInteger>();
         this.updates = new AtomicLong();
      }
      
      public int getUpdateCount(String key){
         AtomicInteger count = counts.get(key);
         
         if(count != null) {
            return count.get();
         }
         return 0;
      }
      
      public long getUpdateCount() {
         return updates.get();
      }
      
      public Set<String> getKeys(){
         return messages.keySet();
      }
      
      public Map<String, Object> getMessage(String key) {
         return messages.get(key);
      }

      @Override
      public void onUpdate(Tuple tuple) {
         Map<String, Object> value = tuple.getAttributes();
         String key = (String)value.get("key");
         AtomicInteger count = counts.get(key);
         
         System.err.println(tuple);
         
         if(count == null) {
            count = new AtomicInteger();
            counts.put(key, count);
         }
         count.getAndIncrement();         
         messages.put(key, value);
         updates.getAndIncrement();
      }

      @Override
      public void onException(Exception cause) {
         System.err.print("onHeartbeat(");
         cause.printStackTrace();
         System.err.println(")");
      }

      @Override
      public void onHeartbeat() {
         System.err.println("onHeartbeat()");
      }

      @Override
      public void onReset() {
         System.err.println("onReset()");
      }  
   }  
}
