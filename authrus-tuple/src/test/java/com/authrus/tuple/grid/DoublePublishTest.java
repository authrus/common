package com.authrus.tuple.grid;

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
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class DoublePublishTest extends TestCase {

   public void testDoublePublish() throws Exception {
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);
      DirectSocketBuilder connector = new DirectSocketBuilder(new TraceAgent(), "localhost", 37773);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(connector, reactor);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener registryListener = new SessionRegistryListener(monitor);
      Subscriber subscriber = new GridSubscriber(registryListener, transportBuilder);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, registryListener, 37773);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      
      grids.put("message", grid);
      predicates.put("message", "*");
      
      server.start();
      
      DoublePublishListener listener = new DoublePublishListener();
      
      subscriber.subscribe(listener, query);
      
      Tuple message1 = createMessage("message1", "name1", "value1", System.currentTimeMillis());
      Tuple message2 = createMessage("message2", "name2", "value2", System.currentTimeMillis());
      
      assertEquals(listener.getUpdateCount(), 0);
      
      publisher.publish(message1);
      publisher.publish(message2);
      
      Thread.sleep(2000); // wait for everything to load and flush out

      assertEquals(listener.getUpdateCount(), 2);
      
      publisher.publish(message1);
      
      Thread.sleep(2000); // wait for everything to flush out
      
      assertEquals(listener.getUpdateCount(), 2);   
      
      publisher.publish(message2);
      
      Thread.sleep(2000); // wait for everything to flush out
   }
   
   public Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   public static class DoublePublishListener implements TupleListener {
      
      private final Map<String, Map<String, Object>> messages;
      private final Map<String, AtomicInteger> counts;
      private final AtomicLong updates;
      
      public DoublePublishListener() {
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
         System.err.print("onException(");
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
