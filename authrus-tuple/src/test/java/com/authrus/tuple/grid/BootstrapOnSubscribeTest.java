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

public class BootstrapOnSubscribeTest extends TestCase {
   
   private static final int PORT = 37513;
   private static final long EXPIRY = 5000;
 
   public void testBootstrapOnSubscribe() throws Exception { 
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener listener = new SessionRegistryListener(monitor);
      Subscriber subscriber = new GridSubscriber(listener, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, listener, PORT);
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
      
      BootstrapUpdateListener listener1 = new BootstrapUpdateListener();
      BootstrapUpdateListener listener2 = new BootstrapUpdateListener();
      
      Tuple message1 = createMessage("message1", "name1", "value1", System.currentTimeMillis());
      Tuple message2 = createMessage("message2", "name2", "value2", System.currentTimeMillis());
      Tuple message3 = createMessage("message3", "name3", "value3", System.currentTimeMillis());
      Tuple message4 = createMessage("message4", "name4", "value4", System.currentTimeMillis());
      
      assertEquals(listener1.getUpdateCount(), 0);
      assertEquals(listener2.getUpdateCount(), 0);
      
      publisher.publish(message1);
      publisher.publish(message2);
      publisher.publish(message3);
      publisher.publish(message4);
      
      assertEquals(listener1.getUpdateCount(), 0);
      assertEquals(listener2.getUpdateCount(), 0);
      
      subscriber.subscribe(listener1, query);
      
      Thread.sleep(2000); // wait for everything too hookup and dispatch
      
      assertEquals(listener1.getUpdateCount(), 4);
      assertEquals(listener2.getUpdateCount(), 0);
      assertEquals(listener1.getUpdateCount("message1"), 1);
      assertEquals(listener1.getUpdateCount("message2"), 1);
      assertEquals(listener1.getUpdateCount("message3"), 1);
      assertEquals(listener1.getUpdateCount("message4"), 1);
      assertEquals(listener1.getMessage("message1").get("name"), "name1");
      assertEquals(listener1.getMessage("message2").get("name"), "name2");
      assertEquals(listener1.getMessage("message3").get("name"), "name3");
      assertEquals(listener1.getMessage("message4").get("name"), "name4");
      
      Thread.sleep(EXPIRY); // wait for expiry time
      
      Tuple updateForMessage2 = createMessage("message2", "updateName2", "updateValue2", System.currentTimeMillis());
      Tuple newMessage5 = createMessage("message5", "name5", "value5", System.currentTimeMillis());
      
      publisher.publish(updateForMessage2);
      publisher.publish(newMessage5);
      
      subscriber.subscribe(listener2, query);
      
      Thread.sleep(2000); // wait for everything to hookup
      
      assertEquals(listener1.getUpdateCount(), 6);
      assertEquals(listener2.getUpdateCount(), 2);
      assertEquals(listener1.getUpdateCount("message1"), 1);
      assertEquals(listener1.getUpdateCount("message2"), 2);
      assertEquals(listener1.getUpdateCount("message3"), 1);
      assertEquals(listener1.getUpdateCount("message4"), 1);
      assertEquals(listener1.getUpdateCount("message5"), 1);      
      assertEquals(listener1.getMessage("message1").get("name"), "name1");
      assertEquals(listener1.getMessage("message2").get("name"), "updateName2");
      assertEquals(listener1.getMessage("message3").get("name"), "name3");
      assertEquals(listener1.getMessage("message4").get("name"), "name4");
      assertEquals(listener1.getMessage("message5").get("name"), "name5");    
      assertEquals(listener2.getMessage("message2").get("name"), "updateName2");
      assertEquals(listener2.getMessage("message5").get("name"), "name5");
   }   
   
   public Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   public static class BootstrapUpdateListener implements TupleListener {
      
      private final Map<String, Map<String, Object>> messages;
      private final Map<String, AtomicInteger> counts;
      private final AtomicLong updates;
      
      public BootstrapUpdateListener() {
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








