package com.authrus.message.grid;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import com.authrus.common.thread.ThreadPool;
import com.authrus.common.time.SampleAverager;
import com.authrus.io.DataDispatcher;
import com.authrus.message.Message;
import com.authrus.message.MessagePublisher;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.message.bind.ObjectMarshaller;
import com.authrus.message.tuple.TupleMessagePublisher;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeListener;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.DeltaProducer;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.Schema;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.PredicateFilter;

public class ChangeSubscriberTest extends TestCase {

   private static final int INPUT_ITERATIONS = 1000000;

   public void testDeltaProduction() throws Exception {
      String[] index = new String[] { "product", "company" };
      Structure structure = new Structure(index);
      OrderMarshaller marshaller = new OrderMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();

      marshallers.put(Order.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      SampleAverager averager = new SampleAverager();
      DataDispatcher dispatcher = new OrderDeltaDispatcher(averager);
      PredicateFilter filter = new PredicateFilter(Collections.EMPTY_MAP);
      DeltaProducer producer = new DeltaProducer(dispatcher, filter);
      ThreadPool pool = new ThreadPool(10);
      ChangeSubscriber subscriptions = new ChangeSubscriber(pool);
      
      Grid grid = new Grid(subscriptions, structure, Order.class.getName());
      Order object = new Order("AU3TB0000124", "1.34", "3.1", "100000", "CITI");
      Map<String, Object> attributes = binder.fromObject(object, Order.class.getName());
      Tuple tuple = new Tuple(attributes, Order.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(Order.class.getName(), grid);
      subscriptions.subscribe("user1", producer);
      grid.update(tuple);

      Tuple recovered = grid.find("AU3TB0000124.CITI");
      Map<String, Object> recoveredAttributes = recovered.getAttributes();      
      Order recoveredObject = (Order) binder.toObject(recoveredAttributes, Order.class.getName());

      assertNotNull(recoveredObject);
      assertEquals(recoveredObject.product, "AU3TB0000124");
      assertEquals(recoveredObject.bid, "1.34");
      assertEquals(recoveredObject.offer, "3.1");
      assertEquals(recoveredObject.volume, "100000");
      assertEquals(recoveredObject.company, "CITI");

      Thread.sleep(500);

      System.err.println("Delta Size: " + averager.average());
      System.err.println("Delta Count: " + averager.count());
      averager.reset();

      long startTime = System.currentTimeMillis();

      for (int i = 0; i < INPUT_ITERATIONS; i++) {
         Order update = new Order("AU3TB0000124", i + ".0", i + ".0", "100000", "CITI");
         Message updateMessage = new Message(update);

         adapter.publish(updateMessage);
      }

      long endTime = System.currentTimeMillis();

      System.err.println("Time taken to update " + INPUT_ITERATIONS + " objects was " + (endTime - startTime) + " milliseconds");

      Thread.sleep(500);

      System.err.println("Delta Average Size: " + averager.average());
      System.err.println("Delta Max Size: " + averager.maximum());
      System.err.println("Delta Min Size: " + averager.minimum());
      System.err.println("Delta Count: " + averager.count());
   }

   public void testSimpleSubscriptions() throws Exception {
      String[] index = new String[] { "product" };
      Structure structure = new Structure(index);
      OrderMarshaller marshaller = new OrderMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();

      marshallers.put(Order.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      AtomicLong updates = new AtomicLong();
      OrderChangeListener counter = new OrderChangeListener(updates);
      ThreadPool pool = new ThreadPool(10);
      ChangeSubscriber subscriptions = new ChangeSubscriber(pool);
      Grid grid = new Grid(subscriptions, structure, Order.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(Order.class.getName(), grid);
      Order object = new Order("AU3TB0000124", "1.34", "3.1", "100000", "CITI");
      Message message = new Message(object);

      subscriptions.subscribe("user1", counter);
      adapter.publish(message);

      Tuple recovered = grid.find("AU3TB0000124");
      Map<String, Object> recoveredAttributes = recovered.getAttributes();      
      Order recoveredObject = (Order) binder.toObject(recoveredAttributes, Order.class.getName());

      assertNotNull(recoveredObject);
      assertEquals(recoveredObject.product, "AU3TB0000124");
      assertEquals(recoveredObject.bid, "1.34");
      assertEquals(recoveredObject.offer, "3.1");
      assertEquals(recoveredObject.volume, "100000");
      assertEquals(recoveredObject.company, "CITI");

      Thread.sleep(500);

      System.err.println(updates);

      long startTime = System.currentTimeMillis();

      for (int i = 0; i < INPUT_ITERATIONS; i++) {
         Order update = new Order("AU3TB0000124", i + ".0", i + ".0", "100000", "CITI");
         Message updateMessage = new Message(update);

         adapter.publish(updateMessage);
      }

      long endTime = System.currentTimeMillis();

      System.err.println("Time taken to update " + INPUT_ITERATIONS + " objects was " + (endTime - startTime) + " milliseconds");

      Thread.sleep(500);

      System.err.println(updates);
   }

   private static class OrderDeltaDispatcher implements DataDispatcher {

      private final SampleAverager averager;

      public OrderDeltaDispatcher(SampleAverager averager) {
         this.averager = averager;
      }

      @Override
      public void dispatch(ByteBuffer buffer) {
         averager.sample(buffer.capacity() - buffer.remaining());
      }
   }

   private static class OrderChangeListener implements ChangeListener {

      private final AtomicLong count;

      public OrderChangeListener(AtomicLong count) {
         this.count = count;
      }

      @Override
      public void onChange(Grid grid, Schema schema, String type) {
         count.getAndIncrement();
      }

   }

   private static class Order {

      public final String product;
      public final String bid;
      public final String offer;
      public final String volume;
      public final String company;

      public Order(String product, String bid, String offer, String volume, String company) {
         this.product = product;
         this.bid = bid;
         this.offer = offer;
         this.volume = volume;
         this.company = company;
      }
   }

   public static class OrderMarshaller implements ObjectMarshaller<Order> {

      @Override
      public Map<String, Object> fromObject(Order object) {
         Map<String, Object> data = new HashMap<String, Object>();

         data.put("product", object.product);
         data.put("bid", object.bid);
         data.put("offer", object.offer);
         data.put("volume", object.volume);
         data.put("company", object.company);

         return data;
      }

      @Override
      public Order toObject(Map<String, Object> data) {
         return new Order((String) data.get("product"), (String) data.get("bid"), (String) data.get("offer"), (String) data.get("volume"), (String) data.get("company"));
      }

   }
}
