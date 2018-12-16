package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.TuplePublisher;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class RandomConcurrentUpdateTest extends TestCase {
   
   private static final long TEST_DURATION = 60000;
   private static final int THREADS = 20;
   private static final int POSSIBLE_KEYS = 400;
   private static final int POSSIBLE_COLUMNS = 100;
   private static final int POSSIBLE_VALUES = 10;
   private static final int SUBSCRIBERS = 10;
   
   public void setUp() {
      ConsoleAppender console = new ConsoleAppender(); // create appender
      // configure the appender
      String PATTERN = "%d [%p|%c] %m%n";
      console.setLayout(new PatternLayout(PATTERN));
      console.setThreshold(Level.INFO);
      console.activateOptions();
      // add appender to any Logger (here is root)
      Logger.getRootLogger().addAppender(console);
   }
 
   public void testManyThreadsUpdatingConcurrently() throws Exception {    
      /*Thread thread = new Thread(new Runnable() {
         public void run() {
            ThreadDumper dumper = new ThreadDumper();
            
            while(true) {
               try {
                  Thread.sleep(1000);
                  System.out.println(dumper.dumpThreads());
               }catch(Exception e){
                  e.printStackTrace();
               }
            }
         }
      });
      thread.start();*/
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);      
      List<RandomConcurrentUpdateListener> listeners = new ArrayList<RandomConcurrentUpdateListener>();
      
      for(int i = 0; i < SUBSCRIBERS; i++) {
         RandomConcurrentUpdateListener listener = new RandomConcurrentUpdateListener(i);
         listeners.add(listener);
      }
      DirectSocketBuilder connector = new DirectSocketBuilder(new TraceAgent(), "localhost", 37773);
      SessionRegistry checker = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(checker);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(connector, reactor); 
      Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, tracer, 37773);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      CountDownLatch startLatch = new CountDownLatch(THREADS + 1);
      CountDownLatch stopLatch = new CountDownLatch(THREADS);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      RandomMessageCollector collector = new RandomMessageCollector();
      
      grids.put("message", grid);
      predicates.put("message", "*");
      server.start();
      
      for(RandomConcurrentUpdateListener listener : listeners) {
         subscriber.subscribe(listener, query);
      }      
      for(int i = 0; i < THREADS; i++) {
         RandomConcurrentClient client = new RandomConcurrentClient(publisher, collector, startLatch, stopLatch, i, TEST_DURATION);
         client.start();
      }
      startLatch.countDown(); // Kick it all off
      
      stopLatch.await();      
      
      Thread.sleep(5000); // Sleep to let things cool down and for all messages to finish flushing
     
      System.err.println("Finished the test with sent " + collector.getUpdateCount());
      
      for(RandomConcurrentUpdateListener listener : listeners) {
         System.err.println("Finished the test with listener " + listener.getUpdateCount());
      }

      Set<String> keys = collector.getKeys();

      for (String key : keys) {
         for(RandomConcurrentUpdateListener listener : listeners) {
            System.err.println("Update count for " + key + " on listener1 is " + listener.getUpdateCount(key));
         }
      }
      Thread.sleep(5000);      
      
      for(RandomConcurrentUpdateListener listener : listeners) {
         for (String key : keys) {
            Map<String, Object> actualFields = collector.getMessage(key);
            Map<String, Object>  receivedFields = listener.getMessage(key);
            Set<String> actualFieldNames = actualFields.keySet();
            Set<String> receivedFieldNames = receivedFields.keySet();
   
            for (String fieldName : actualFieldNames) {
               Object actualValue = actualFields.get(fieldName);
               Object receivedValue = receivedFields.get(fieldName);
   
               assertEquals("Field name was " + fieldName + " actual=" + actualValue + " received=" + receivedValue, actualValue, receivedValue);
            }
            for (String fieldName : receivedFieldNames) {
               Object actualValue = actualFields.get(fieldName);
               Object receivedValue = receivedFields.get(fieldName);
   
               assertEquals("Field name was " + fieldName + " actual=" + actualValue + " received=" + receivedValue, actualValue, receivedValue);
            }
            //System.err.println("All equal for " + key + " sent=" + actualFields + " received=" + receivedFields);
         }
      }
   }   
   
   public static class RandomConcurrentClient extends Thread {
      
      private final RandomMessageCollector messageCollector;
      private final TuplePublisher messagePublisher;
      private final FastRandom randomGenerator;
      private final CountDownLatch startLatch;
      private final CountDownLatch stopLatch;
      private final long testDuration;
      private final int clientId;
      
      public RandomConcurrentClient(TuplePublisher messagePublisher, RandomMessageCollector messageCollector, CountDownLatch startLatch, CountDownLatch stopLatch, int clientId, long testDuration) {
         this.randomGenerator = new FastRandom();
         this.messageCollector = messageCollector;
         this.messagePublisher = messagePublisher;
         this.startLatch = startLatch;
         this.stopLatch = stopLatch;    
         this.testDuration = testDuration;
         this.clientId = clientId;
      }
      
      public void run() {
         try {
            long startTime = System.currentTimeMillis();
            long expiryTime = startTime + testDuration;
            long currentTime = startTime;
            boolean showDebug = true;
            
            startLatch.countDown();
            startLatch.await();
            
            System.err.println("Started client " + clientId + " -> " + Thread.currentThread().getName());
            
            Thread.sleep(2000);
            
            while(currentTime < expiryTime) {
               try {
                  if(showDebug) {
                     if(startTime + 10000 < currentTime) {
                        System.err.println("Going in to silent mode for improved throughput and performance");
                        showDebug = false;
                     }
                  }
                  String key = String.valueOf(randomGenerator.nextInt(POSSIBLE_KEYS) + ":" + clientId);
                  Tuple tuple = createRandomConcurrentUpdate(key, showDebug);
                  Map<String, Object> value = tuple.getAttributes();
                  
                  messageCollector.setMessage(key, value);
                  messagePublisher.publish(tuple);
               } catch(Exception e) {
                  e.printStackTrace();
               } finally {
                  currentTime = System.currentTimeMillis();
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         } finally {
            stopLatch.countDown();
            
            System.err.println("Finished client " + clientId);
         }
      }
      
      public Tuple createRandomConcurrentUpdate(String key, boolean debug) {
         Map<String, Object> previousMessage = messageCollector.getMessage(key);
         Map<String, Object> map = new HashMap<String, Object>();
         
         if(previousMessage == null) {
            previousMessage = new HashMap<String, Object>();
         }
         map.putAll(previousMessage);
         map.put("key", key);
                  
         int columnsToUse = randomGenerator.nextInt(POSSIBLE_COLUMNS); // randomly choosing columns causes staggered growth of schema
         
         for(int i = 0; i < columnsToUse; i++) {
            int randomValue = randomGenerator.nextInt(POSSIBLE_VALUES);
            String column = String.valueOf(i);
            Object previous = previousMessage.get(column);
            
            if(previous != null) {
               if(randomValue % 4 != 0) { // replace 25% of the time
                  map.put(column, randomValue);
               } else {
                  if(randomValue % 3 == 0) { // use some null values also
                     map.put(column, null);
                  }
               }
            } else {
               map.put(column, randomValue);
            }
         }
         if(debug) { // show the percentage difference
            if(previousMessage != null) {
               Set<String> columns = map.keySet();
               float count = columns.size();
               float difference = 0;        
               
               for(String column : columns) {
                  Object value = map.get(column);
                  Object previous = previousMessage.get(column);
                  
                  if(value != previous) {
                     if(value != null && previous != null) {
                        if(!value.equals(previous)) {
                           difference++;
                        }
                     }
                  }
               }
               System.err.println("For key " + key + " the message was " + Math.round(((count - difference)/ count) * 100) + "% similar to previous");
            }
         }
         
         messageCollector.setMessage(key, map); // keep previous so we can randomly replace values
         
         return new Tuple(map, "message");
      }
   }
   
   public static class RandomMessageCollector {
      
      private final Map<String, Map<String, Object>> messages;
      private final AtomicLong updates;
      
      public RandomMessageCollector() {
         this.messages = new ConcurrentHashMap<String, Map<String, Object>>();
         this.updates = new AtomicLong();
      }
      
      public long getUpdateCount() {
         return updates.get();
      }
      
      public Set<String> getKeys(){
         return messages.keySet();
      }
      
      public void setMessage(String key, Map<String, Object> message) {
         messages.put(key, message);
         updates.getAndIncrement();
      }
      
      public Map<String, Object> getMessage(String key) {
         Map<String, Object> previous = messages.get(key);
         
         if(previous != null) {
            return Collections.unmodifiableMap(previous);
         }
         return null;
      }
   }
   
   public static class RandomConcurrentUpdateListener implements TupleListener {
      
      private final Map<String, Map<String, Object>> messages;
      private final Map<String, AtomicInteger> counts;
      private final AtomicLong updates;
      private final int index;
      
      public RandomConcurrentUpdateListener(int index) {
         this.messages = new ConcurrentHashMap<String, Map<String, Object>>();
         this.counts = new ConcurrentHashMap<String, AtomicInteger>();
         this.updates = new AtomicLong();
         this.index = index;
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
         //if(index == 0) {
         //   System.err.println(message);
         //}
         count.getAndIncrement();         
         messages.put(key, value);
         updates.getAndIncrement();
      }

      @Override
      public void onException(Exception cause) {
         System.err.print("["+index+"] onException(");
         cause.printStackTrace();
         System.err.println(")");
      }

      @Override
      public void onHeartbeat() {
         System.err.println("["+index+"] onHeartbeat()");
      }

      @Override
      public void onReset() {
         System.err.println("["+index+"] onReset()");
      }  
   }  
   
   private static class FastRandom extends Random {

      private long seed;

      public FastRandom() {
         this.seed = System.currentTimeMillis();
      }

      protected int next(int nbits) {
         long x = seed;
         x ^= (x << 21);
         x ^= (x >>> 35);
         x ^= (x << 4);
         seed = x;
         x &= ((1L << nbits) - 1);
         return (int) x;
      }

   }
}








