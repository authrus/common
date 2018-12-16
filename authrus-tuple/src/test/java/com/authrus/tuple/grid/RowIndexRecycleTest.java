package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.authrus.tuple.grid.record.DeltaRecord;
import com.authrus.tuple.grid.record.DeltaRecordListener;
import com.authrus.tuple.grid.record.DeltaScanner;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class RowIndexRecycleTest extends TestCase {

   private static final int PORT = 34351;
   private static final long EXPIRY = 5000;
   private static final int MESSAGES = 20000;
   private static final int GRID_CAPACITY = 4000;
   
   public void testRotatingIndexes() throws Exception {
      LocalListener deltaInterceptor = new LocalListener();
      DeltaScanner scanner = new DeltaScanner(deltaInterceptor, true);
      String[] primaryKey = new String[] { "key"};
      Structure structure = new Structure(primaryKey);
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", PORT);      
      FrameAdapter listener = new FrameAdapter();
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      Subscriber subscriber = new GridSubscriber(listener, transportBuilder, 5000, EXPIRY);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      Map<String, Tuple> sent = new ConcurrentHashMap<String, Tuple>();
      Map<String, Tuple> previous = new ConcurrentHashMap<String, Tuple>();      
      SubscriptionLogger logger = new SubscriptionLogger();
      GridServer server = new GridServer(changeSubscriber, logger, scanner, PORT);
      Grid grid = new Grid(changeSubscriber, structure, "message", GRID_CAPACITY); 
      Map<String, String> predicates = new HashMap<String, String>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      RowIndexRecycleMessageCollector messageListener = new RowIndexRecycleMessageCollector();
      Random random = new Random();
      
      grids.put("message", grid);
      predicates.put("message", "*");      
      server.start();  
      
      subscriber.subscribe(messageListener, query); 
      int prev = 0;
      for(int x = 0; x < 5; x++) {
         for(int i = 0; i < MESSAGES; i++) {
            int real = random.nextInt(MESSAGES);
            String key = "key-"+real;
            long time = System.currentTimeMillis();
            Tuple message = createMessage(key, "name(key="+key+")", "value(key="+key+", index="+i+", time="+time+", iter="+x+")", time);
            int size = sent.size();
            
            if(size % 100 == 0 && size != MESSAGES && size != prev) {
               System.err.println("SIZE IS NOW " + size); 
               prev = size;
            }            
            publisher.publish(message);
            Tuple p = sent.get(key);
            if(p  != null){
               previous.put(key, p);
            }
            sent.put(key, message);            
         }
         int size = sent.size();
         
         System.err.println("SIZE IS NOW " + size + " ITERATION IS " + x);         
      }
      for(int i =0; i < 5; i++) {
         System.err.println(5 -i);
         Thread.sleep(1000);
      }
      messageListener.debug.set(true);
      
      
      Set<String> keys = messageListener.messages.keySet();
      //assertEquals("Should only be one validator for one subscription", grid.vals.size(), 1);
      //DeltaValidator val = grid.vals.iterator().next();
     
      for(String key : keys) {
         Tuple messageReceived = messageListener.messages.get(key);
         Tuple messageSent = sent.get(key);
         Tuple messagePrev = previous.get(key);
         Map<String, Object> valueReceived = messageReceived.getAttributes();
         Map<String, Object> valueSent = messageSent.getAttributes();         
         Map<String, Object> valuePrev = null;
         //String state = val.getState("message", key);
         Long arrivalTime = messageListener.arrival.get(key);
         //String data = messageListener.data.get(key);
         String sentOut = deltaInterceptor.messages.get(key);
         Long sentOutTime = deltaInterceptor.arrival.get(key);
         
         if(messagePrev != null){
            valuePrev = messagePrev.getAttributes();
         }         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))
               +" sentTime="+timeDiff((Long)valueSent.get("time")) + " arrivalTime="+timeDiff(arrivalTime)
               + " prev="+valuePrev 
              // +" state="+state
               //+ " data=" + data
               + " sentOut="+sentOut + " sentOutTime="+timeDiff(sentOutTime), 
               valueReceived.get("value"), 
               valueSent.get("value"));
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))
               +" sentTime="+timeDiff((Long)valueSent.get("time")) + " arrivalTime="+timeDiff(arrivalTime)
               + " prev="+valuePrev 
               //+" state="+state
               //+ " data=" + data 
               + " sentOut="+sentOut + " sentOutTime="+timeDiff(sentOutTime), 
               valueReceived.get("name"), 
               valueSent.get("name"));         
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))
               +" sentTime="+timeDiff((Long)valueSent.get("time")) + " arrivalTime="+timeDiff(arrivalTime)
               + " prev="+valuePrev 
              // +" state="+state
               //+ " data=" + data
               + " sentOut="+sentOut + " sentOutTime="+timeDiff(sentOutTime),  
               valueReceived.get("key"), 
               valueSent.get("key"));
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))
               +" sentTime="+timeDiff((Long)valueSent.get("time")) + " arrivalTime="+timeDiff(arrivalTime)
               + " prev="+valuePrev 
               //+" state="+state
               //+ " data=" + data
               + " sentOut="+sentOut + " sentOutTime="+timeDiff(sentOutTime),  
               valueReceived.get("time"), 
               valueSent.get("time"));
      }
      keys = sent.keySet(); // validate both ways
      
      for(String key : keys) {
         Tuple messageReceived = messageListener.messages.get(key);
         Tuple messageSent = sent.get(key);
         Map<String, Object> valueReceived = messageReceived.getAttributes();
         Map<String, Object> valueSent = messageSent.getAttributes();
         
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))+" sentTime="+timeDiff((Long)valueSent.get("time")), valueReceived.get("value"), valueSent.get("value"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))+" sentTime="+timeDiff((Long)valueSent.get("time")), valueReceived.get("name"), valueSent.get("name"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))+" sentTime="+timeDiff((Long)valueSent.get("time")), valueReceived.get("key"), valueSent.get("key"));
         assertEquals("received="+valueReceived.get("key")+" sent="+valueSent.get("key") +" receivedTime="+timeDiff((Long)valueReceived.get("time"))+" sentTime="+timeDiff((Long)valueSent.get("time")), valueReceived.get("time"), valueSent.get("time"));
      }
   }
   public static long timeDiff(Long time) {
      return System.currentTimeMillis() - time;
      
   }
   
   private Tuple createMessage(String key, String name, String value, long time) {
      Map<String, Object> message = new HashMap<String, Object>();
      message.put("key", key);
      message.put("name", name);
      message.put("value", value);
      message.put("time", time);
      return new Tuple(message, "message");
   }
   
   public static class LocalListener implements DeltaRecordListener {
      
      public final Map<String, String> messages;
      public final Map<String, Long> arrival;
      
      public LocalListener() {
         this.messages = new ConcurrentHashMap<String, String>();
         this.arrival = new ConcurrentHashMap<String, Long>(); 
      }

      @Override
      public void onUpdate(Session session, DeltaRecord record) {
         Row row = record.getMerge().getCurrent();
         String name = row.getKey();
         
         messages.put(name, ""+row);
         arrival.put(name, System.currentTimeMillis());
      }

      @Override
      public void onReset(Session session) {
         System.err.println("onReset"+session+")");
      }
      
   }
   
   private static class RowIndexRecycleMessageCollector implements TupleListener {
      
      public final Map<String, Tuple> messages;
      public final Map<String, Long> arrival;
      public final AtomicBoolean debug;
      
      public RowIndexRecycleMessageCollector() {
         this.arrival = new ConcurrentHashMap<String, Long>(); 
         this.messages = new ConcurrentHashMap<String, Tuple>();
         this.debug = new AtomicBoolean();
      }
      
      @Override
      public void onUpdate(Tuple tuple) {        
         Map<String, Object> value = tuple.getAttributes();
         String key = (String)value.get("key");
         String text = (String)value.get("value");
         
         if(text.indexOf(key) == -1) {
            throw new IllegalStateException("Value of "+value+" does not match key " + key + " for "+value);
         }
         if(debug.get()) {
            System.err.println("MessageListener.onMessage(): key="+key+" message="+value);
         }
         arrival.put(key, System.currentTimeMillis());
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
