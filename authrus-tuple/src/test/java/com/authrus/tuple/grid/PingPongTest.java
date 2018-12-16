package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.Subscription;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class PingPongTest extends TestCase {
   
   private static final int PORT = 32513;
   private static final long EXPIRY = 5000;

   public void testPingPing() throws Exception {
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);      
      SessionLogger listener = new SessionLogger();
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);      
      Subscriber subscriber = new GridSubscriber(listener, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      Map<String, Tuple> sent = new ConcurrentHashMap<String, Tuple>();
      final Map<String, Tuple> received = new ConcurrentHashMap<String, Tuple>();
      SubscriptionLogger logger = new SubscriptionLogger() {
         @Override
         public void onUpdate(String address, Tuple message) {
            received.put(message.getAttributes().get("key").toString(), message);
         }  
      };
      GridServer server = new GridServer(changeSubscriber, logger, listener, PORT);
      Grid grid = new Grid(changeSubscriber, structure, "message");
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      TupleListener messageListener = new PingPingListener();    
      
      grids.put("message", grid);
      predicates.put("message", "*");      
      server.start();      
      
      Tuple message1 = createMessage("message1", "name1", "value1", System.currentTimeMillis());
      Tuple message2 = createMessage("message2", "name2", "value2", System.currentTimeMillis());
      Tuple message3 = createMessage("message3", "name3", "value3", System.currentTimeMillis());
      Tuple message4 = createMessage("message4", "name4", "value4", System.currentTimeMillis());
      
      publisher.publish(message1);
      publisher.publish(message2);
      publisher.publish(message3);
      publisher.publish(message4);      
      
      Subscription subscription = subscriber.subscribe(messageListener, query);      
      
      subscription.publish(message1);
      sent.put(message1.getAttributes().get("key").toString(), message1);
      
      for(int i = 0; i < 100000; i++) {
         Tuple publishMessage = createMessage("message"+i, "name"+i, "value"+i, System.currentTimeMillis());
         subscription.publish(publishMessage);
         sent.put(publishMessage.getAttributes().get("key").toString(), publishMessage);
      }      
      Thread.sleep(5000);
      System.err.println("SUCCESS="+listener.success.get()+" FAILURES="+listener.failure.get());
      
      Set<String> keys = received.keySet();
      for(String key : keys) {
         Tuple messageReceived = received.get(key);
         Tuple messageSent = sent.get(key);
         Map<String, Object> valueReceived = messageReceived.getAttributes();
         Map<String, Object> valueSent = messageSent.getAttributes();
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("value"), valueSent.get("value"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("name"), valueSent.get("name"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("key"), valueSent.get("key"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("time"), valueSent.get("time"));
      }
      keys = sent.keySet(); // validate both ways
      
      for(String key : keys) {
         Tuple messageReceived = received.get(key);
         Tuple messageSent = sent.get(key);
         Map<String, Object> valueReceived = messageReceived.getAttributes();
         Map<String, Object> valueSent = messageSent.getAttributes();
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("value"), valueSent.get("value"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("name"), valueSent.get("name"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("key"), valueSent.get("key"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key"), valueReceived.get("time"), valueSent.get("time"));
      }
   }
   
   private Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   private static class SessionLogger extends FrameAdapter {
      
      private AtomicLong success;
      private AtomicLong failure;
      
      public SessionLogger() {
         this.success = new AtomicLong();
         this.failure = new AtomicLong();         
      }
      
      @Override
      public void onSuccess(Session session, int sequence) {
         System.err.println("onSuccess("+session+", "+sequence+")");
         success.getAndIncrement();
      }
      
      @Override
      public void onFailure(Session session, int sequence) {
         System.err.println("!!!!!!!!!!!!!!!!! onFailure("+session+", "+sequence+")");
         failure.getAndIncrement();
      } 
   }
   
   private static class PingPingListener implements TupleListener {

      @Override
      public void onUpdate(Tuple tuple) {
         System.err.println("onUpdate("+tuple+")");
      }

      @Override
      public void onException(Exception cause) {
         System.err.println("onException("+cause+")");
         cause.printStackTrace();
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
