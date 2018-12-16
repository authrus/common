package com.authrus.attribute;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.TestCase;

public class AttributeMarshallerMapPerformanceTest extends TestCase {   

   private static final int ITERATIONS = 100000;
   
   public static void main(String[] list) throws Exception {
      new AttributeMarshallerMapPerformanceTest().testMarshallerPerformance();
   }

   private static class SelfValidatingMessageWithMap implements Serializable {

      public String key;
      public int sumOfAll;
      public Map<String, Integer> value;

      public SelfValidatingMessageWithMap(Map<String, Integer> value) {
         this.value = value;
      }

      public int calculateSum() {
         return value.get("value0") + value.get("value1")
               + value.get("value2") + value.get("value3") 
               + value.get("value4") + value.get("value5") 
               + value.get("value6") + value.get("value7")
               + value.get("value8") + value.get("value9");
      }

      public boolean isValid() {
         return calculateSum() == sumOfAll;
      }
   }

   public void testMarshallerPerformance() throws Exception {
      ConcurrentLinkedQueue<SelfValidatingMessageWithMap> records = new ConcurrentLinkedQueue<SelfValidatingMessageWithMap>();
      ConcurrentLinkedQueue<Map<String, Object>> serialized = new ConcurrentLinkedQueue<Map<String, Object>>();
      Random random = new FastRandom();

      for (int i = 0; i < ITERATIONS; i++) {
         String messageKey = "key-" + random.nextInt(ITERATIONS);
         Map<String, Integer> value = new HashMap<String, Integer>();
         SelfValidatingMessageWithMap message = new SelfValidatingMessageWithMap(value);

         message.key = messageKey;
         message.value.put("value0", random.nextInt(100000));
         message.value.put("value1", random.nextInt(100000));
         message.value.put("value2", random.nextInt(100000));
         message.value.put("value3", random.nextInt(100000));
         message.value.put("value4", random.nextInt(100000));
         message.value.put("value5", random.nextInt(100000));
         message.value.put("value6", random.nextInt(100000));
         message.value.put("value7", random.nextInt(100000));
         message.value.put("value8", random.nextInt(100000));
         message.value.put("value9", random.nextInt(100000));
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

      for (int i = 0; i < 10; i++) { // prime test
         SelfValidatingMessageWithMap record = records.poll();

         if (record != null) {
            Map<String, Object> map = marshaller.fromObject(record);
            System.err.println(map);
            serialized.offer(map);
         }
      }
      for (int i = 0; i < 10; i++) { // prime test
         Map<String, Object> map = serialized.poll();

         if (map != null) {
            SelfValidatingMessageWithMap record = (SelfValidatingMessageWithMap) marshaller.toObject(map);
            records.offer(record);
         }
      }
      for (int x = 0; x < 500; x++) {
         System.err.println("STARTING SERIALIZATION TEST...");

         long startTime = System.currentTimeMillis();
         int count = 0;

         while (!records.isEmpty()) {
            SelfValidatingMessageWithMap record = records.poll();

            if (record != null) {
               Map<String, Object> map = marshaller.fromObject(record);
               serialized.offer(map);
               count++;
            }
         }
         long endTime = System.currentTimeMillis();

         System.err.println("(SERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + (count / ((endTime - startTime)) * 1000) + " PER SECOND");

         System.err.println("STARTING DESERIALIZATION TEST...");

         startTime = System.currentTimeMillis();
         count = 0;

         while (!serialized.isEmpty()) {
            Map<String, Object> map = serialized.poll();

            if (map != null) {
               SelfValidatingMessageWithMap record = (SelfValidatingMessageWithMap) marshaller.toObject(map);

               if (!record.isValid()) {
                  assertTrue("Record was not valid", false);
               }
               records.offer(record);
               count++;
            }
         }
         endTime = System.currentTimeMillis();

         System.err.println("(DESERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + (count / ((endTime - startTime)) * 1000) + " PER SECOND");
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
