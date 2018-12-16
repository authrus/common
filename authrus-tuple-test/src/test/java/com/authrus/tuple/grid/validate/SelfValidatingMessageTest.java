package com.authrus.tuple.grid.validate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.authrus.transport.trace.TraceLogger;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class SelfValidatingMessageTest extends TestCase {

   private static int ITERATIONS = 100000000;
   private static int MESSAGES = 1000;
   
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

   public void testSelfValidatingMessages() throws Exception {
      Random random = new FastRandom();
      MemoryMonitor memoryMonitor = new MemoryMonitor();
      
      memoryMonitor.start();
      
      Map<String, Tuple> previousMessages = new HashMap<String, Tuple>();
      String[] key = new String[] { "key" };
      Structure structure = new Structure(key);
      AtomicInteger successes = new AtomicInteger();
      AtomicInteger failures = new AtomicInteger();
      SelfValidatingUpdateListener listener = new SelfValidatingUpdateListener("SelfValidatingMessageMarshaller", successes, failures);
      //TraceLogger socketMonitor = new TraceLogger();
      //TraceAgent analyzer = new TraceAgent(socketMonitor);
      TraceAgent analyzer = new TraceAgent();
      DirectSocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", 32353);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(monitor);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, tracer, 32353);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      
      //socketMonitor.start(); VERY VERBOSE
      predicates.put("message", "*"); 
      listener.start();
      server.start();
      subscriber.subscribe(listener, query);
      
      String[] keys = new String[MESSAGES];

      for (int i = 0; i < ITERATIONS; i++) {
         int index = random.nextInt(MESSAGES);
         String messageKey = keys[index];
         
         if(messageKey == null) {
            messageKey = "key-" + index;
            keys[index] = messageKey;
         }
         Tuple previousMessage = previousMessages.get(messageKey);
         Map<String, Object> attributes = new HashMap<String, Object>();
         Tuple tuple = new Tuple(attributes, "message");      

         if (previousMessage != null) {
            Integer[] previousValues = new Integer[] { 
                  (Integer)previousMessage.getAttributes().get("value0"), 
                  (Integer)previousMessage.getAttributes().get("value1"), 
                  (Integer)previousMessage.getAttributes().get("value2"), 
                  (Integer)previousMessage.getAttributes().get("value3"), 
                  (Integer)previousMessage.getAttributes().get("value4"), 
                  (Integer)previousMessage.getAttributes().get("value5"),
                  (Integer)previousMessage.getAttributes().get("value6"), 
                  (Integer)previousMessage.getAttributes().get("value7"), 
                  (Integer)previousMessage.getAttributes().get("value8"), 
                  (Integer)previousMessage.getAttributes().get("value9") 
            };
            int start = random.nextInt(5);
            List<Integer> previousValuesList = new ArrayList(Arrays.asList(previousValues));
            List<Integer> subList = previousValuesList.subList(start, start + 5);

            Collections.shuffle(subList, random);

            attributes.put("key", messageKey);
            attributes.put("sumOfAll", previousMessage.getAttributes().get("sumOfAll"));
            attributes.put("value0", previousValuesList.get(0));
            attributes.put("value1", previousValuesList.get(1));
            attributes.put("value2", previousValuesList.get(2));
            attributes.put("value3", previousValuesList.get(3));
            attributes.put("value4", previousValuesList.get(4));
            attributes.put("value5", previousValuesList.get(5));
            attributes.put("value6", previousValuesList.get(6));
            attributes.put("value7", previousValuesList.get(7));
            attributes.put("value8", previousValuesList.get(8));
            attributes.put("value9", previousValuesList.get(9));
            attributes.put("time", System.nanoTime());
         } else {    
            int[] newRandomValues = new int[] { 
                  random.nextInt(100000),//0
                  random.nextInt(100000),//1
                  random.nextInt(100000),//2
                  random.nextInt(100000),//3
                  random.nextInt(100000),//4
                  random.nextInt(100000),//5
                  random.nextInt(100000),//6
                  random.nextInt(100000),//7
                  random.nextInt(100000),//8
                  random.nextInt(100000)//9                
            };
            int sumOfValues = 
                  newRandomValues[0] +
                  newRandomValues[1] +
                  newRandomValues[2] +
                  newRandomValues[3] +
                  newRandomValues[4] +
                  newRandomValues[5] +
                  newRandomValues[6] +
                  newRandomValues[7] +
                  newRandomValues[8] +
                  newRandomValues[9]; 
            
            attributes.put("key", messageKey);
            attributes.put("sumOfAll", sumOfValues);
            attributes.put("value0", newRandomValues[0]);
            attributes.put("value1", newRandomValues[1]);
            attributes.put("value2", newRandomValues[2]);
            attributes.put("value3", newRandomValues[3]);
            attributes.put("value4", newRandomValues[4]);
            attributes.put("value5", newRandomValues[5]);
            attributes.put("value6", newRandomValues[6]);
            attributes.put("value7", newRandomValues[7]);
            attributes.put("value8", newRandomValues[8]);
            attributes.put("value9", newRandomValues[9]);
            attributes.put("time", System.nanoTime());              
         }
         previousMessages.put(messageKey, tuple);
         grid.update(tuple);
      }
      listener.kill();
      Thread.sleep(5000);

      assertEquals(failures.get(), 0);
   }
   
   private static class MemoryMonitor extends Thread {
      
      public void run() {
         try {
            Runtime runtime = Runtime.getRuntime();
            DecimalFormat format = new DecimalFormat("#.##");
            while(true) {
               Thread.sleep(1000);
               double total = runtime.totalMemory();
               double free = runtime.freeMemory();
               double used = total - free;
               
               System.err.println("used="+used+" percent="+format.format((used / total) * 100.0) + "%");
            }
               
         } catch(Exception e){
            e.printStackTrace();
         }
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
