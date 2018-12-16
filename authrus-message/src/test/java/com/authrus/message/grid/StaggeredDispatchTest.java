package com.authrus.message.grid;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import com.authrus.common.thread.ThreadPool;
import com.authrus.io.ByteBufferReader;
import com.authrus.io.DataConsumer;
import com.authrus.io.DataDispatcher;
import com.authrus.io.DataReader;
import com.authrus.message.Message;
import com.authrus.message.MessageAdapter;
import com.authrus.message.MessagePublisher;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.message.bind.ObjectMarshaller;
import com.authrus.message.tuple.TupleMessageListener;
import com.authrus.message.tuple.TupleMessagePublisher;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.DeltaDispatcher;
import com.authrus.tuple.grid.DeltaProducer;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.PredicateFilter;

public class StaggeredDispatchTest extends TestCase {
   
   public void testDispatch() throws Exception {
      String[] index = new String[] { "name" };
      Structure structure = new Structure(index);      
      ExampleObjectMarshaller marshaller = new ExampleObjectMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
      Map<String, String> predicates = new HashMap<String, String>();
      
      predicates.put(ExampleObject.class.getName(), "*");
      marshallers.put(ExampleObject.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      ThreadPool pool = new ThreadPool(10);
      ChangeSubscriber subscriber = new ChangeSubscriber(pool);
      Grid grid = new Grid(subscriber, structure, ExampleObject.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(ExampleObject.class.getName(), grid);
      
      ExampleObjectListener messageListener = new ExampleObjectListener();
      TupleMessageListener adapterListener = new TupleMessageListener(messageListener, binder);
      DataConsumer dataConsumer = new DeltaDispatcher(adapterListener);

      PredicateFilter filter = new PredicateFilter(predicates);
      DataDispatcher dataDispatcher = new DataBridgeAdapter(dataConsumer);
      DeltaProducer deltaProducer = new DeltaProducer(dataDispatcher, filter);
      Map<String, Integer> expectCounts = new HashMap<String, Integer>();
      
      subscriber.subscribe("test", deltaProducer);
      
      for(int i = 0; i < 1000; i++) {
         adapter.publish(new Message(new ExampleObject("key-" + i, "value-" + i, System.currentTimeMillis())));
         expectCounts.put("key-"+i,  1);
      }
      adapter.publish(new Message(new ExampleObject("key-of-second-last-message", "value-of-second-last-message", System.currentTimeMillis())));
      Thread.sleep(5000);
      adapter.publish(new Message(new ExampleObject("key-of-last-message", "value-of-last-message", System.currentTimeMillis())));
      Thread.sleep(1000);
      adapter.publish(new Message(new ExampleObject("key-of-last-message", "another-value-for-last-message", System.currentTimeMillis())));
      Thread.sleep(2000);  
      //Thread.sleep(100000000);
      for(int i = 0; i < 1000; i++) {
         assertEquals(messageListener.getCount("key-" + i), expectCounts.get("key-"+i).intValue());         
         assertEquals(messageListener.getValue("key-" + i), "value-" + i);
      }
      assertEquals(messageListener.getCount("key-of-second-last-message"), 1);
      assertEquals(messageListener.getValue("key-of-second-last-message"), "value-of-second-last-message");
      assertEquals(messageListener.getCount("key-of-last-message"), 2);
      assertEquals(messageListener.getValue("key-of-last-message"), "another-value-for-last-message");
   }
   
   private static class DataBridgeAdapter implements DataDispatcher {

      private final DataConsumer dataConsumer;
      
      public DataBridgeAdapter(DataConsumer dataConsumer) {
         this.dataConsumer = dataConsumer;
      }
      
      @Override
      public void dispatch(ByteBuffer buffer) throws Exception {
         DataReader reader = new ByteBufferReader(buffer);
         
         dataConsumer.consume(reader);
      }
      
   }
   
   private static class ExampleObjectListener extends MessageAdapter {
      
      private final Map<String, AtomicInteger> counts;
      private final Map<String, String> values;
      
      public ExampleObjectListener() {
         this.counts = new ConcurrentHashMap<String, AtomicInteger>();
         this.values = new ConcurrentHashMap<String, String>();
      }
      
      public synchronized String getValue(String key){
         return values.get(key);
      }
      
      public synchronized int getCount(String key) {
         return counts.get(key).get();
      }
      
      public synchronized void onMessage(Message message) {
         ExampleObject object = (ExampleObject)message.getValue();
         AtomicInteger count = counts.get(object.name);
         
         if(count == null) {
            count = new AtomicInteger();
            counts.put(object.name, count);
         }
         count.getAndIncrement();
         values.put(object.name, object.value);
         System.err.println("name="+object.name+" value="+object.value+" time="+object.time+" delay="+(System.currentTimeMillis()-object.time));
      }

   }

   private static class ExampleObject {

      public final String name;
      public final String value;
      public final long time;

      public ExampleObject(String name, String value, long time) {
         this.name = name;
         this.value = value;
         this.time = time;
      }
   }

   public static class ExampleObjectMarshaller implements ObjectMarshaller<ExampleObject> {

      @Override
      public Map<String, Object> fromObject(ExampleObject object) {
         Map<String, Object> data = new HashMap<String, Object>();

         data.put("name", object.name);
         data.put("value", object.value);
         data.put("time", object.time);

         return data;
      }

      @Override
      public ExampleObject toObject(Map<String, Object> data) {
         String name = (String)data.get("name");
         String value = (String)data.get("value");
         long time = (Long)data.get("time");
         
         return new ExampleObject(name, value, time);
      }
   }

}
