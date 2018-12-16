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

public class GridExpiryTest extends TestCase {

   private static final int EXPIRY = 5000;
   
   public void testExpiryWithNormalOperation() throws Exception {
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
      DataConsumer dataConsumer = new DeltaDispatcher(adapterListener, EXPIRY);
      PredicateFilter filter = new PredicateFilter(predicates);
      DataDispatcher dataDispatcher = new DataBridgeAdapter(dataConsumer);
      DeltaProducer deltaProducer = new DeltaProducer(dataDispatcher, filter);

      subscriber.subscribe("test", deltaProducer);
      
      for(int i = 0; i < 1000; i++) {
         adapter.publish(new Message(new ExampleObject(String.format("name-%s", i), "dateOfBirth-X", "placeOfBirth-X", "country-X", "job-X", "address-X")));
      }
      Thread.sleep(EXPIRY + 1000);
      
      for(int x =  0; x < 10; x++) {
         for(int i = 0; i < 1000; i++) {
            adapter.publish(new Message(new ExampleObject(
                  String.format("name-%s", i), 
                  String.format("dateOfBirth-%s-%s", i, x), 
                  String.format("placeOfBirth-%s-%s", i, x), 
                  String.format("country-%s-%s", i, x), 
                  String.format("job-%s-%s", i, x), 
                  String.format("address-%s-%s", i, x))));
         }
         Thread.sleep(1000);
         
         for(int i = 0; i < 1000; i++) {            
            assertEquals(messageListener.getValue("name-" + i).name, String.format("name-%s", i));
            assertEquals(messageListener.getValue("name-" + i).dateOfBirth, String.format("dateOfBirth-%s-%s", i, x));
            assertEquals(messageListener.getValue("name-" + i).placeOfBirth, String.format("placeOfBirth-%s-%s", i, x));
            assertEquals(messageListener.getValue("name-" + i).country, String.format("country-%s-%s", i, x));
            assertEquals(messageListener.getValue("name-" + i).job, String.format("job-%s-%s", i, x));
            assertEquals(messageListener.getValue("name-" + i).address, String.format("address-%s-%s", i, x));
         }
      }
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
      private final Map<String, ExampleObject> values;
      
      public ExampleObjectListener() {
         this.counts = new ConcurrentHashMap<String, AtomicInteger>();
         this.values = new ConcurrentHashMap<String, ExampleObject>();
      }
      
      public synchronized ExampleObject getValue(String key){
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
         values.put(object.name, object);
      }

   }

   private static class ExampleObject {

      public final String name;
      public final String dateOfBirth;
      public final String placeOfBirth;
      public final String country;
      public final String job;
      public final String address;

      public ExampleObject(String name, String dateOfBirth, String placeOfBirth, String country, String job, String address) {
         this.name = name;
         this.dateOfBirth = dateOfBirth;
         this.placeOfBirth = placeOfBirth;
         this.country = country;
         this.job = job;
         this.address = address;
      }
   }

   public static class ExampleObjectMarshaller implements ObjectMarshaller<ExampleObject> {

      @Override
      public Map<String, Object> fromObject(ExampleObject object) {
         Map<String, Object> data = new HashMap<String, Object>();

         data.put("name", object.name);
         data.put("dateOfBirth", object.dateOfBirth);
         data.put("placeOfBirth", object.placeOfBirth);
         data.put("country", object.country);
         data.put("job", object.job);
         data.put("address", object.address);

         return data;
      }

      @Override
      public ExampleObject toObject(Map<String, Object> data) {
         return new ExampleObject(
               (String) data.get("name"), 
               (String) data.get("dateOfBirth"), 
               (String) data.get("placeOfBirth"), 
               (String) data.get("country"), 
               (String) data.get("job"),
               (String) data.get("address"));
      }

   }

}
