package com.authrus.attribute;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import junit.framework.TestCase;

import com.authrus.attribute.ComplexObjectTest.ExampleObject;

public class MapWithIntegerKeyTest extends TestCase {
   
   public static class Price implements Serializable {
      int productId;
      double price;
      char side;
      public Price(int productId, double price, char side){
         this.price = price;
         this.side = side;
      }
      public char getSide(){
         return side;
      }
      public double getPrice(){
         return price;
      }
      public int getProductId() {
         return productId;
      }
   }
   public static class Rate implements Serializable{
      Map<Integer, Price> prices;
      Set<Integer> ids;
      String name;
      public Rate(String name) {
         this.prices = new ConcurrentHashMap<Integer, Price>();
         this.ids = new CopyOnWriteArraySet<Integer>();
         this.name = name;
      }
      public void addPrice(Price price){
         prices.put(price.productId, price);
      }
      public Price getPrice(int productId){
         return prices.get(productId);
      }
      public Set<Integer> getIds(){
         return Collections.unmodifiableSet(ids);
      }
      public String getName(){
         return name;       
      }
   }
   public static class State implements Serializable {
      Map<String, Rate> rates;
      public State() {
         this.rates = new ConcurrentHashMap<String, Rate>();
      }
      public void addRate(Rate rate){
         rates.put(rate.name, rate);
      }
      public Rate getRate(String name){
         return rates.get(name);
      }
   }

   public void testMap() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      AttributeConverter marshaller = new AttributeConverter(serializer);
      ExampleObject object = new ExampleObject();
      State state = new State();
      for(int i = 0; i < 0; i++) {
         Rate rate = new Rate("rate-"+i);
         state.addRate(rate);
         for(int j = 0; j < 10; j++) {
            if(i % 2 == 0) {
               rate.addPrice(new Price(j, j, 'B'));
            } else {
               rate.addPrice(new Price(j, j, 'S'));
            }
         }        
      }
      Map<String, Object> map = marshaller.fromObject(state);
      System.err.println(new TreeMap<String, Object>(map));
      State recovered = (State)marshaller.toObject(map);
      assertNotNull(recovered);
      for(int i = 0; i < 0; i++) {
         Rate rate = recovered.getRate("rate-"+i);
         assertEquals(rate.name, "rate-"+i);
         for(int j = 0; j < 10; j++) {
            Price price = rate.getPrice(j);
            assertNotNull(price);
            assertEquals(price.price, (double)j);
            assertEquals(price.productId, j);
            if(i % 2 == 0) {
               assertEquals(price.side, 'B');
            } else {
               assertEquals(price.side, 'S');
            }
         }        
      }
      
   }
}
