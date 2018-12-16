package com.authrus.tuple.subscribe;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;
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

public class SubscriptionPublishPerformanceTest extends TestCase {
   
   private static final Logger LOG = LoggerFactory.getLogger(SubscriptionPublishPerformanceTest.class);
   
   private static final int PORT = 33413;
   private static final long EXPIRY = 50000;

   public void testChangeSubscription() throws Exception {      
      LOG.info("testChangeSubscription()");      
      String[] primaryKey = new String[] { "key"};
      String[] constants = new String[] { "value"};      
      Structure structure = new Structure(primaryKey, constants);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener listener = new SessionRegistryListener(monitor);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor); 
      Subscriber subscriber = new GridSubscriber(listener, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionListenerTimer logger = new SubscriptionListenerTimer();
      GridServer server = new GridServer(changeSubscriber, logger, listener, PORT);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessageCollector collector = new MessageCollector();
      
      grids.put("message", grid);
      predicates.put("message", "value == 'value1' || value == 'value3'");
      server.start();
      
      Tuple message1 = createMessage("message1", "name1", "value1", System.currentTimeMillis());
      Tuple message2 = createMessage("message2", "name2", "value2", System.currentTimeMillis());
      Tuple message3 = createMessage("message3", "name3", "value3", System.currentTimeMillis());
      Tuple message4 = createMessage("message4", "name4", "value4", System.currentTimeMillis());      
      
      publisher.publish(message1);
      publisher.publish(message2);
      publisher.publish(message3);
      publisher.publish(message4);
      
      assertTrue(collector.messages.isEmpty());      
      
      Subscription subscription = subscriber.subscribe(collector, query);
      
      Thread.sleep(2000);
      
      assertFalse(collector.messages.isEmpty());
      assertEquals(collector.messages.size(), 2);
      assertEquals(collector.messages.get("message1").getAttributes().get("value"), "value1");
      assertEquals(collector.messages.get("message3").getAttributes().get("value"), "value3");
      
      //collector.messages.clear();
      
      //assertTrue(collector.messages.isEmpty());
      //assertEquals(collector.messages.size(), 0);
      
      predicates.put("message", "value == 'value1' || value == 'value2'");
      subscription.update(query);
      
      Thread.sleep(2000);
      
      assertFalse(collector.messages.isEmpty());
      assertEquals(collector.messages.size(), 3);
      assertEquals(collector.messages.get("message1").getAttributes().get("value"), "value1"); // this gets no update
      assertEquals(collector.messages.get("message2").getAttributes().get("value"), "value2");
      assertEquals(collector.messages.get("message3").getAttributes().get("value"), "value3"); // this gets no update
      assertNull(collector.messages.get("message4"));
      
      
      for(int i = 0; i < 1000000; i++) {
         subscription.publish(message1);
      }      
   }   
   
   public Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("value1", value);
      message.put("value2", value);
      message.put("value3", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   private static class SubscriptionListenerTimer extends SubscriptionAdapter {

      private final AtomicLong failures;
      private final AtomicLong successes;
      private final AtomicLong samples;
      private final DecimalFormat format;
      private final Thread thread;
      
      public SubscriptionListenerTimer() {
         this.format = new DecimalFormat("#.##");
         this.failures = new AtomicLong();
         this.successes = new AtomicLong();
         this.samples = new AtomicLong();
         this.thread = new Thread(new Runnable() {
               @Override         
               public void run() {
                  try {
                     while (true) {
                        Thread.sleep(1000);               
                        if(samples.get() > 0) {
                           //long averageMicros = (totalTime.get() / successes.get());
                           long sampleCount = samples.getAndSet(0);
                           long averageMillis = (sampleCount / 1000);               
                           
                           System.err.println("throughput=" + format.format(sampleCount) + " failures=" + failures.get() + " total=" + format.format(successes.get()) + " average-millis=" + averageMillis);
                        }
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               } 
         });
         thread.start();
      }
      
      public void onUpdate(String address, Tuple tuple) {
         successes.getAndIncrement();
         samples.getAndIncrement();
      }
      
      public void onException(String address, Exception cause) {
         cause.printStackTrace();
         failures.getAndIncrement();
      }
   }
   
   
   private static class MessageCollector implements TupleListener {
      
      public final Map<String, Tuple> messages;
      
      public MessageCollector() {
         this.messages = new ConcurrentHashMap<String, Tuple>();
      }
      
      @Override
      public void onUpdate(Tuple tuple) {
         Map<String, Object> value = tuple.getAttributes();
         String key = (String)value.get("key");
         System.err.println(value);
         messages.put(key, tuple);
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
