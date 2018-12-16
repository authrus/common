package com.authrus.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import junit.framework.TestCase;

public class StreamPerformanceTest extends TestCase {
   
   public static void main(String[] list) throws Exception {
      new StreamPerformanceTest().testStreamPerformance();
      new StreamPerformanceTest().testStreamEncodeOnlyPerformance();
   }

   private static final int ITERATIONS = 10000000;
   
   private static class Encoder {
      
      public void encode(Message message, DataWriter writer) throws Exception {
         writer.writeString(message.product);
         writer.writeDouble(message.price);
         writer.writeInt(message.volume);
         writer.writeString(message.trader);
         writer.writeChar(message.side);
         writer.writeString(message.benchmark);
         writer.writeBoolean(message.firm);
         writer.writeString(message.company);
      }
      
      public Message decode(DataReader reader) throws Exception {
         Message message = new Message();
         message.product = reader.readString();
         message.price = reader.readDouble();
         message.volume = reader.readInt();
         message.trader = reader.readString();
         message.side = reader.readChar();
         message.benchmark = reader.readString();
         message.firm = reader.readBoolean();
         message.company = reader.readString();
         return message;
      }
   }
   
   public static class Message {
      String product;
      double price;
      int volume;
      String trader;
      char side;
      String benchmark;
      boolean firm;
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
         
         if(recovered.price != message.price){
            throw new IllegalStateException("Invalid price " + recovered.price);
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
         
         if(recovered.price != message.price){
            throw new IllegalStateException("Invalid price " + recovered.price);
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
