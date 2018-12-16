package com.authrus.message.serialize;

import java.io.Serializable;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;

public class AttributeMarshallerPerformanceTest extends TestCase {

   private static final int ITERATIONS = 100000;

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
      BlockingQueue<SelfValidatingMessage> records = new LinkedBlockingQueue<SelfValidatingMessage>();
      BlockingQueue<Map<String, Object>> serialized = new LinkedBlockingQueue<Map<String, Object>>();
      Random random = new SecureRandom();

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
      AttributeMarshaller marshaller = new AttributeMarshaller(serializer);
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
      for (int x = 0; x < 5; x++) {
         System.err.println("STARTING SERIALIZATION TEST...");
         System.gc();
         Thread.sleep(1000);

         double startTime = System.currentTimeMillis();
         double count = 0;

         while (!records.isEmpty()) {
            SelfValidatingMessage record = records.poll();

            if (record != null) {
               Map<String, Object> map = marshaller.fromObject(record);
               serialized.offer(map);
               count++;
            }
         }
         double endTime = System.currentTimeMillis();

         if(endTime > startTime) {
            System.err.println("(SERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + format.format(count / ((endTime - startTime)) * 1000.0d) + " PER SECOND");
         }

         System.err.println("STARTING DESERIALIZATION TEST...");
         Thread.sleep(1000);

         startTime = System.currentTimeMillis();
         count = 0;

         while (!serialized.isEmpty()) {
            Map<String, Object> map = serialized.poll();

            if (map != null) {
               SelfValidatingMessage record = (SelfValidatingMessage) marshaller.toObject(map);

               if (!record.isValid()) {
                  assertTrue("Record was not valid", false);
               }
               records.offer(record);
               count++;
            }
         }
         endTime = System.currentTimeMillis();

         if(endTime > startTime) {
            System.err.println("(DESERIALIZATION) TOTAL FOR " + count + ": " + (endTime - startTime) + " WHICH IS " + format.format(count / ((endTime - startTime)) * 1000.0d) + " PER SECOND");
         }
         System.err.println();
         System.err.println();
      }
   }
}
