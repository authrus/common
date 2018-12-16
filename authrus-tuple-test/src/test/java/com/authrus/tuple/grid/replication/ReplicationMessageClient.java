package com.authrus.tuple.grid.replication;

import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.ITERATIONS;
import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.MESSAGES;
import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.PRIMARY_PORT;
import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.SECONDARY_PORT;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

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
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.Subscription;

public class ReplicationMessageClient {   

   public static void main(String[] list) throws Exception {
      ConsoleAppender console = new ConsoleAppender(); 
      PatternLayout layout = new PatternLayout("%d [%p|%c|%C{1}] %m%n");

      console.setLayout(layout);
      console.setThreshold(Level.INFO);
      console.activateOptions();
      
      Logger.getRootLogger().addAppender(console);
      
      Map<String, Tuple> primaryMessages = new ConcurrentHashMap<String, Tuple>();
      Map<String, Tuple> secondaryMessages = new ConcurrentHashMap<String, Tuple>();      
      ReplicationClient primary = new ReplicationClient(primaryMessages, "primary", PRIMARY_PORT);
      ReplicationClient secondary = new ReplicationClient(secondaryMessages, "secondary", SECONDARY_PORT);
      
      primary.setName("PrimaryClient");
      secondary.setName("SecondaryClient");
      primary.start();
      secondary.start();
      primary.join();
      secondary.join();
      
      Thread.sleep(1000);
      System.err.println("Reconciling all messages");
      
      Set<String> keys = primaryMessages.keySet();
      
      for (String key : keys) {
         Map<String, Object> actualFields = primaryMessages.get(key).getAttributes();
         Map<String, Object>  receivedFields = secondaryMessages.get(key).getAttributes();
         Set<String> actualFieldNames = actualFields.keySet();
         Set<String> receivedFieldNames = receivedFields.keySet();

         for (String fieldName : actualFieldNames) {
            Object actualValue = actualFields.get(fieldName);
            Object receivedValue = receivedFields.get(fieldName);

            Assert.assertEquals("Field name was " + fieldName + " actual=" + actualValue + " received=" + receivedValue, actualValue, receivedValue);
         }
         for (String fieldName : receivedFieldNames) {
            Object actualValue = actualFields.get(fieldName);
            Object receivedValue = receivedFields.get(fieldName);

            Assert.assertEquals("Field name was " + fieldName + " actual=" + actualValue + " received=" + receivedValue, actualValue, receivedValue);
         }
         System.err.println("All equal for " + key + " sent=" + actualFields + " received=" + receivedFields);
      }      
      System.exit(1);
   }
   
   public static class ReplicationClient extends Thread {      
         
      private final Map<String, Tuple> messages;
      private final String prefix;
      private final int port;
      
      public ReplicationClient(Map<String, Tuple> messages, String prefix, int port) {
         this.messages = messages;
         this.prefix = prefix;
         this.port = port;
      }
      
      public void run() {
         try {   
            Random random = new Random();
            AtomicInteger successes = new AtomicInteger();
            AtomicInteger failures = new AtomicInteger();
            ReplicationMessageListener listener = new ReplicationMessageListener(messages, prefix, successes, failures);
            DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", port);
            SessionRegistry monitor = new SessionRegistry();
            SessionRegistryListener tracer = new SessionRegistryListener(monitor);
            ThreadPool pool = new ThreadPool(10);
            Reactor reactor = new ExecutorReactor(pool);
            DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
            Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
            Map<String, String> predicates = new HashMap<String, String>();
            Origin origin = new Origin("test");
            Query query = new Query(origin, predicates);
      
            predicates.put("message", "*");
            listener.start();
            
            Subscription subscription = subscriber.subscribe(listener, query);      
            
            for (int i = 0; i < ITERATIONS; i++) {
               int value = random.nextInt(MESSAGES);
               String name = "name-"+value+"-"+prefix;
               String job = "job-" +value;
               String address = "address"+value;
               long time = System.nanoTime();
               if(job.endsWith("2") || job.endsWith("4") || job.endsWith("5")) {
                  job = null;
               }
               Map<String, Object> data = new HashMap<String, Object>();             
               Tuple tuple = new Tuple(data, "message");
               
               data.put("name", name);
               data.put("job", job);
               data.put("address", address);
               data.put("time", time);
               
               subscription.publish(tuple);
               Thread.sleep(5);
            }
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
