package com.authrus.message.serialize;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.message.serialize.AttributeMarshaller;

import junit.framework.TestCase;

public class AttributeMarshallerMapPerformanceTest extends TestCase {

   private static final int ITERATIONS = 10000;

   private static class SelfValidatingMessageWithMap implements Serializable {

      public String key;
      public int sumOfAll;
      public Map<String, Integer> value;

      public SelfValidatingMessageWithMap(Map<String, Integer> value) {
         this.value = value;
      }

      public int calculateSum() {
         return value.get("value0") + value.get("value1") + value.get("value2") + value.get("value3") + value.get("value4") + value.get("value5") + value.get("value6") + value.get("value7")
               + value.get("value8") + value.get("value9");
      }

      public boolean isValid() {
         return calculateSum() == sumOfAll;
      }
   }

   public void testMarshallerPerformance() throws Exception {
      BlockingQueue<SelfValidatingMessageWithMap> records = new LinkedBlockingQueue<SelfValidatingMessageWithMap>();
      BlockingQueue<Map<String, Object>> serialized = new LinkedBlockingQueue<Map<String, Object>>();
      Random random = new SecureRandom();

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
      AttributeMarshaller marshaller = new AttributeMarshaller(serializer);

      for (int i = 0; i < 10; i++) { // prime test
         SelfValidatingMessageWithMap record = records.poll();

         if (record != null) {
            Map<String, Object> map = marshaller.fromObject(record);
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
      for (int x = 0; x < 5; x++) {
         System.err.println("STARTING SERIALIZATION TEST...");
         System.gc();
         Thread.sleep(1000);

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
         Thread.sleep(1000);

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
   }
}
