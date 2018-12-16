package com.authrus.tuple.queue;

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
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleAdapter;
import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class QueuePerformanceTest extends TestCase {

   private static final int MESSAGE_COUNT = 1000000 * 10;
   private static final int PORT = 23431;
   private static final int EXPIRY = 5000;
   
   @Override
   public void setUp() {
      ConsoleAppender console = new ConsoleAppender(); // create appender
      // configure the appender
      String PATTERN = "%d [%p|%c|%C{1}] %m%n";
      console.setLayout(new PatternLayout(PATTERN));
      console.setThreshold(Level.INFO);
      console.activateOptions();
      // add appender to any Logger (here is root)
      Logger.getRootLogger().addAppender(console);
   }
   
   public void testDispatch() throws Exception {     
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Queue> queues = new HashMap<String, Queue>();
      
      predicates.put("message", "*");
      
      ThreadPool pool = new ThreadPool(10);
      InsertSubscriber insertSubscriber = new InsertSubscriber(pool);
      Queue queue = new Queue(insertSubscriber, "message");
      ExampleObjectListener messageListener = new ExampleObjectListener();
      SubscriptionLogger logger = new SubscriptionLogger();
      FrameAdapter tracer = new FrameAdapter();
      QueueServer server = new QueueServer(insertSubscriber, logger, tracer, PORT);
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      QueuePublisher publisher = new QueuePublisher(queues);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener listener = new SessionRegistryListener(monitor);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);      
      Subscriber subscriber = new QueueSubscriber(listener, transportBuilder, 5000);
      
      queues.put("message", queue);
      predicates.put("message", "*");      
      server.start();
      subscriber.subscribe(messageListener, query);
      
      Thread.sleep(2000);
 
      /*for(int i = 0; i < 29; i++) {
         ExampleObject object = new ExampleObject("key-" + i, "value-" + i, System.currentTimeMillis(), i);
         Message message = new Message(object);
         
         publisher.publish(message);
      }
      System.err.println("WAITING!");
      Thread.sleep(1000);*/
      
      for(int i = 0; i < MESSAGE_COUNT; i++) {
         Map<String, Object> data = new HashMap<String, Object>();
         Tuple tuple = new Tuple(data, "message");
         
         data.put("name", "key-" + i);
         data.put("value", "value-" + i);
         data.put("time", System.currentTimeMillis());
         data.put("index",  i);
         
         publisher.publish(tuple);
      }
   }
   
   private static class ExampleObjectListener extends TupleAdapter {     
      
      private final AtomicLong failures;
      private final AtomicLong successes;
      private final AtomicLong samples;
      private final DecimalFormat format;
      private final AtomicInteger counter;
      private final Thread thread;
      
      public ExampleObjectListener() {
         this.format = new DecimalFormat("#.##");
         this.counter = new AtomicInteger();
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
      
      @Override
      public void onUpdate(Tuple tuple) {
         Map<String, Object> attributes = tuple.getAttributes();
         int index = (Integer)attributes.get("index");
         int count = counter.getAndIncrement();         
         
         //System.err.println("received=" + index + " expected=" + count);
                  
         successes.getAndIncrement();
         samples.getAndIncrement();
      }
      
      public void onException(Exception cause) {
         cause.printStackTrace();
         failures.getAndIncrement();
      }

   }
}
