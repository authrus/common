package com.authrus.attribute;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.TestCase;

public class AttributeMarshallerPerformanceTest extends TestCase {

   private static final int ITERATIONS = 100000;
   
   public static void main(String[] list) throws Exception {
      new AttributeMarshallerPerformanceTest().testMarshallerPerformance();
   }

   private static class SelfValidatingMessage implements Serializable {

      public String key;
      public int sumOfAll;
      public int value0;
      public int value1;
      public int value2;
      public int value3;
      public int value4;
      public int value5;
      public int value6;
      public int value7;
      public int value8;
      public int value9;

      public int calculateSum() {
         return value0 + value1 + value2 + value3 + value4 + value5 + value6 + value7 + value8 + value9;
      }

      public boolean isValid() {
         return calculateSum() == sumOfAll;
      }
   }

   public void testMarshallerPerformance() throws Exception {
      ConcurrentLinkedQueue<SelfValidatingMessage> records = new ConcurrentLinkedQueue<SelfValidatingMessage>();
      ConcurrentLinkedQueue<Map<String, Object>> serialized = new ConcurrentLinkedQueue<Map<String, Object>>();
      Random random = new FastRandom();

      for (int i = 0; i < ITERATIONS; i++) {
         String messageKey = "key-" + random.nextInt(ITERATIONS);
         SelfValidatingMessage message = new SelfValidatingMessage();

         message.key = messageKey;
         message.value0 = random.nextInt(100000);
         message.value1 = random.nextInt(100000);
         message.value2 = random.nextInt(100000);
         message.value3 = random.nextInt(100000);
         message.value4 = random.nextInt(100000);
         message.value5 = random.nextInt(100000);
         message.value6 = random.nextInt(100000);
         message.value7 = random.nextInt(100000);
         message.value8 = random.nextInt(100000);
         message.value9 = random.nextInt(100000);
         message.sumOfAll = message.calculateSum();

         assertTrue(message.isValid());
         records.offer(message);
      }
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      AttributeConverter marshaller = new AttributeConverter(serializer);
      DecimalFormat format = new DecimalFormat("###,###,###.##");

      for (int i = 0; i < 10; i++) { // prime test
         SelfValidatingMessage record = records.poll();

         if (record != null) {
            Map<String, Object> map = marshaller.fromObject(record);
            serialized.offer(map);
         }
      }
      for (int i = 0; i < 10; i++) { // prime test
         Map<String, Object> map = serialized.poll();

         if (map != null) {
            SelfValidatingMessage record = (SelfValidatingMessage) marshaller.toObject(map);
            records.offer(record);
         }
      }
      for (int x = 0; x < 500; x++) {
         System.err.println("STARTING SERIALIZATION TEST...");

         long startTime = System.currentTimeMillis();
         int count = 0;

         System.err.println("records="+records.size());
         
         while (!records.isEmpty()) {
            SelfValidatingMessage record = records.poll();

            if (record == null) {
               throw new IllegalStateException("Map is null");
            }
            Map<String, Object> map = marshaller.fromObject(record);
            serialized.offer(map);
            count++;            
         }
         long endTime = System.currentTimeMillis();

         System.err.println("count="+count+" duration="+(endTime-startTime));        
         System.err.println("(SERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + format.format(count / ((endTime - startTime)) * 1000) + " PER SECOND");

         System.err.println("STARTING DESERIALIZATION TEST...");

         startTime = System.currentTimeMillis();
         count = 0;

         while (!serialized.isEmpty()) {
            Map<String, Object> map = serialized.poll();

            if (map == null) {
               throw new IllegalStateException("Map is null");
            }
            SelfValidatingMessage record = (SelfValidatingMessage) marshaller.toObject(map);

            if (!record.isValid()) {
               assertTrue("Record was not valid", false);
            }
            records.offer(record);
            count++;
         }
         endTime = System.currentTimeMillis();

         System.err.println("count="+count+" duration="+(endTime -startTime));
         System.err.println("(DESERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + format.format(count / ((endTime - startTime)) * 1000) + " PER SECOND");
      }
      System.err.println(marshaller.fromObject(records.poll()));
      System.err.flush();
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
