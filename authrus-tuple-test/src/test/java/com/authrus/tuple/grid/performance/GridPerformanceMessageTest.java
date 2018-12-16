package com.authrus.tuple.grid.performance;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
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
import com.authrus.transport.SocketBuilder;
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

public class GridPerformanceMessageTest extends TestCase {

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
      //Random random = new FastRandom();
      AtomicLong counter = new AtomicLong();
      MemoryMonitor memoryMonitor = new MemoryMonitor(counter);
      
      memoryMonitor.start();
      
      String[] key = new String[] { "Stock" };
      Structure structure = new Structure(key);
      AtomicInteger successes = new AtomicInteger();
      AtomicInteger failures = new AtomicInteger();
      GridPerformanceUpdateListener listener = new GridPerformanceUpdateListener("client", successes, failures);
      //SocketProbeLogger socketMonitor = new SocketProbeLogger();
      //SocketAnalyzer analyzer = new SocketAnalyzer(socketMonitor);
      TraceAgent analyzer = new TraceAgent();      
      SocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", 32353);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(monitor);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, tracer, 32353);
      Grid grid = new Grid(changeSubscriber, structure, "A");
      Map<String, String> predicates = new HashMap<String, String>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      
      //socketMonitor.start(); VERY VERBOSE
      predicates.put("A", "*"); 
      listener.start();
      server.start();
      subscriber.subscribe(listener, query);
      
      String[] stocks = new String[MESSAGES];
      
      for (int i = 0; i < MESSAGES; i++) {
        stocks[i] = "STOCK-"+i;
      }   
      /**
      AddOrder/0x41 : MsgBase -> # 'A'
      u64 OrderRefNo,
      u8 BuySell,
      u32 Shares,
      string Stock,
      u32 Price         
        */
      for(int i = 0; i < ITERATIONS; i++) {
         Map<String, Object> data = new HashMap<String, Object>();
         Tuple tuple = new Tuple(data, "A");
         int index = i % MESSAGES;
         
         data.put("OrderRefNo", (long)i);
         data.put("BuySell", (char)'B');
         data.put("Shares", (long)i);
         data.put("Stock",  stocks[index]);
         data.put("Price",  i);
         data.put("Time", System.nanoTime());         
         
         grid.update(tuple);
         counter.getAndIncrement();
      }
      listener.kill();
      Thread.sleep(5000);

      assertEquals(failures.get(), 0);
   }
   
   
   private static class MemoryMonitor extends Thread {
      
      private final AtomicLong counter;
      
      public MemoryMonitor(AtomicLong counter) {
         this.counter = counter;
      }
      
      public void run() {
         try {
            Runtime runtime = Runtime.getRuntime();
            DecimalFormat format = new DecimalFormat("#.##");
            while(true) {
               Thread.sleep(1000);
               long count = counter.getAndSet(0);
               double total = runtime.totalMemory();
               double free = runtime.freeMemory();
               double used = total - free;
               
               System.err.println("messages="+count+" memory-used="+used+" memory-percent="+format.format((used / total) * 100.0) + "%");
            }
               
         } catch(Exception e){
            e.printStackTrace();
         }
      }
      
   }
   /*
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

   }*/
}
