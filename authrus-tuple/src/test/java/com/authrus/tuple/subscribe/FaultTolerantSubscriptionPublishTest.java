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
import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;

public class FaultTolerantSubscriptionPublishTest extends TestCase  {
   
   private static final Logger LOG = LoggerFactory.getLogger(SubscriptionPublishPerformanceTest.class);
   
   private static final int PORT = 3113;
   private static final long EXPIRY = 50000;
   
   public void testFaultTolerantSubscriptionPublish() throws Exception {
      LOG.info("testFaultTolerantSubscriptionPublish()");  
      String[] primaryKey = new String[] { "key"};
      String[] constants = new String[] { "value"};      
      Structure structure = new Structure(primaryKey, constants);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      FrameAdapter adapter = new FrameAdapter();
      Subscriber subscriber = new GridSubscriber(adapter, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      FaultTolerantSubscriptionListenerTimer listener = new FaultTolerantSubscriptionListenerTimer();
      GridServer server = new GridServer(changeSubscriber, listener, listener, PORT);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      FaultTolerantMessageCollector collector = new FaultTolerantMessageCollector();
      
      grids.put("message", grid);
      predicates.put("message", "*");
      
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
      
      subscription.publish(message1);
      subscription.publish(message2);
      subscription.publish(message3);
      subscription.publish(message4);     
      
      assertTrue(collector.messages.isEmpty()); // nothing received as the server has not started      
      assertTrue(listener.messages.isEmpty());
      
      server.start(); // start the server

      Thread.sleep(10000);
      
      assertFalse(collector.messages.isEmpty()); // client received messages
      assertFalse(listener.messages.isEmpty()); // server received messages
      assertEquals(listener.messages.size(), 4); // we published 4 unique
      assertEquals(collector.messages.size(), 4); // the grid has 4 uniquely keyed messages
      
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
   
   private static class FaultTolerantSubscriptionListenerTimer extends FrameAdapter implements SubscriptionListener {

      public final Map<String, Tuple> messages;
      private final AtomicLong failures;
      private final AtomicLong successes;
      private final AtomicLong samples;
      private final DecimalFormat format;
      private final Thread thread;
      
      public FaultTolerantSubscriptionListenerTimer() {
         this.messages = new ConcurrentHashMap<String, Tuple>();
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
         Map<String, Object> value = tuple.getAttributes();
         String key = (String)value.get("key");
         System.err.println("SERVER RECEIVED: " + value);
         messages.put(key, tuple);
         successes.getAndIncrement();
         samples.getAndIncrement();
      }
      
      public void onException(String address, Exception cause) {
         cause.printStackTrace();
         failures.getAndIncrement();
      }

      @Override
      public void onConnect(String address) {
         LOG.info("Connection established for " + address);
      }

      @Override
      public void onClose(String address) {
         LOG.info("Connection closed for " + address);
      }

      @Override
      public void onSubscribe(String address, Query query) {
         LOG.info("Subscription for " + address + " updated to " + query);
      }   

      @Override
      public void onHeartbeat(String address) {
         LOG.info("Heartbeat for " + address);
      }
   }

   
   private static class FaultTolerantMessageCollector implements TupleListener {
      
      public final Map<String, Tuple> messages;
      
      public FaultTolerantMessageCollector() {
         this.messages = new ConcurrentHashMap<String, Tuple>();
      }
      
      @Override
      public void onUpdate(Tuple message) {
         Map<String, Object> value = message.getAttributes();
         String key = (String)value.get("key");
         System.err.println("CLIENT RECEIVED: " + value);
         messages.put(key, message);
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
