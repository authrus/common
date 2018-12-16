package com.authrus.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import junit.framework.TestCase;

public class StreamWithDataEncoderTest extends TestCase {
   
   public static void main(String[] list) throws Exception {
      new StreamWithDataEncoderTest().testStreamPerformance();
      new StreamWithDataEncoderTest().testStreamEncodeOnlyPerformance();
   }

   private static final int ITERATIONS = 10000000;
   
   private static class Encoder {
      
      DataFormatter writeOnly = new DataFormatter();
      DataFormatter readOnly = new DataFormatter();
      
      public void encode(Message message, DataWriter writer) throws Exception {
         writeOnly.write(writer, message.product);
         writeOnly.write(writer, message.price);
         writeOnly.write(writer, message.volume);
         writeOnly.write(writer, message.trader);
         writeOnly.write(writer, message.side);
         writeOnly.write(writer, message.benchmark);
         writeOnly.write(writer, message.firm);
         writeOnly.write(writer, message.company);
      }
      
      public Message decode(DataReader reader) throws Exception {
         Message message = new Message();
         message.product = (String)readOnly.read(reader);
         message.price = (Double)readOnly.read(reader);
         message.volume = (Integer)readOnly.read(reader);
         message.trader = (String)readOnly.read(reader);
         message.side = (Character)readOnly.read(reader);
         message.benchmark = (String)readOnly.read(reader);
         message.firm = (Boolean)readOnly.read(reader);
         message.company = (String)readOnly.read(reader);
         return message;
      }
   }
   
   public static class Message {
      String product;
      Double price;
      Integer volume;
      String trader;
      Character side;
      String benchmark;
      Boolean firm;
      String company;
   }
   
   public void testStreamEncodeOnlyPerformance() throws Exception {
      SharedByteArray output = new SharedByteArray();     
      DataWriter writer = new OutputStreamWriter(output);
      DecimalFormat format = new DecimalFormat("###,###,###,###.##");
      Encoder encoder = new Encoder();
      
      Message message = new Message();
      message.product = "AU3TB1235456";
      message.price = 3.4566;
      message.volume = 1000000;
      message.trader = "tom@db.com";
      message.side = 'B';
      message.benchmark = "TYBSep12";
      message.firm = true;
      message.company = "DB";
      
      for(int i = 0; i < ITERATIONS; i++) { // warm it up!!
         encoder.encode(message, writer);
         output.reset();
      }      
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < ITERATIONS; i++) {
         encoder.encode(message, writer);
         output.reset();
      }
      long finish = System.currentTimeMillis();
      long duration = finish - start;
      
      System.err.println("Time taken for STREAM ENCODE ONLY " + duration + " ms which is " + format.format(ITERATIONS / (duration / 1000)) + " per second");
   }
   
   public void testStreamPerformance() throws Exception {
      SharedByteArray output = new SharedByteArray();     
      DataWriter writer = new OutputStreamWriter(output);
      DecimalFormat format = new DecimalFormat("###,###,###,###.##");
      Encoder encoder = new Encoder();
      
      Message message = new Message();
      message.product = "AU3TB1235456";
      message.price = 3.4566;
      message.volume = 1000000;
      message.trader = "tom@db.com";
      message.side = 'B';
      message.benchmark = "TYBSep12";
      message.firm = true;
      message.company = "DB";           
      
      for(int i = 0; i < 5000; i++) { // warm it up!!
         encoder.encode(message, writer);
         byte[] result = output.getInternalArray();
         ByteArrayInputStream input = new ByteArrayInputStream(result);
         DataReader reader = new InputStreamReader(input);
         Message recovered = encoder.decode(reader);
         
         if(!recovered.price.equals(message.price)){
            throw new IllegalStateException("Invalid price " + recovered.price + " actual price " + message.price);
         }         
         output.reset();
      }  
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < ITERATIONS; i++) {
         encoder.encode(message, writer);
         byte[] result = output.getInternalArray();
         ByteArrayInputStream input = new ByteArrayInputStream(result);
         DataReader reader = new InputStreamReader(input);
         Message recovered = encoder.decode(reader);
         
         if(!recovered.price.equals(message.price)){
            throw new IllegalStateException("Invalid price " + recovered.price + " actual price " + message.price);
         }
         output.reset();
      }
      long finish = System.currentTimeMillis();
      long duration = finish - start;
      
      System.err.println("Time taken for STREAM " + duration + " ms which is " + format.format(ITERATIONS / (duration / 1000)) + " per second");
   }
   
   private static class SharedByteArray extends ByteArrayOutputStream {
      
      public SharedByteArray() {
         super();
      }
      
      public byte[] getInternalArray(){
         return buf;
      }            
   }
}
