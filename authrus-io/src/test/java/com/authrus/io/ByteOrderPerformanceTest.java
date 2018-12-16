package com.authrus.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferReader;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataReader;
import com.authrus.io.DataWriter;

import junit.framework.TestCase;

public class ByteOrderPerformanceTest extends TestCase {
   
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
   
   public void testLittleEndian() throws Exception {
      ByteBufferBuilder builder = new ByteBufferBuilder();
      ByteBufferWriter writer = new ByteBufferWriter(builder);
      DecimalFormat format = new DecimalFormat("###,###,###,###.##");
      Encoder encoder = new Encoder();
      
      builder.order(ByteOrder.LITTLE_ENDIAN);
      
      Message message = new Message();
      message.product = "AU3TB1235456";
      message.price = 3.4566;
      message.volume = 1000000;
      message.trader = "tom@db.com";
      message.side = 'B';
      message.benchmark = "TYBSep12";
      message.firm = true;
      message.company = "DB";           
      
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < ITERATIONS; i++) {
         encoder.encode(message, writer);
         ByteBuffer result = builder.extract();
         DataReader reader = new ByteBufferReader(result);
         Message recovered = encoder.decode(reader);
         
         if(recovered.price != message.price){
            throw new IllegalStateException("Invalid price " + recovered.price);
         }
         builder.clear();
      }
      long finish = System.currentTimeMillis();
      long duration = finish - start;
      
      System.err.println("Time taken for LITTLE " + duration + " ms which is " + format.format(ITERATIONS / (duration / 1000)) + " per second");
   }
   
   public void testBigEndian() throws Exception {
      ByteBufferBuilder builder = new ByteBufferBuilder();
      ByteBufferWriter writer = new ByteBufferWriter(builder);
      DecimalFormat format = new DecimalFormat("###,###,###,###.##");
      Encoder encoder = new Encoder();
      
      builder.order(ByteOrder.BIG_ENDIAN);
      
      Message message = new Message();
      message.product = "AU3TB1235456";
      message.price = 3.4566;
      message.volume = 1000000;
      message.trader = "tom@db.com";
      message.side = 'B';
      message.benchmark = "TYBSep12";
      message.firm = true;
      message.company = "DB";  
      
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < ITERATIONS; i++) {
         encoder.encode(message, writer);
         ByteBuffer result = builder.extract();
         DataReader reader = new ByteBufferReader(result);
         Message recovered = encoder.decode(reader);
         
         if(recovered.price != message.price){
            throw new IllegalStateException("Invalid price " + recovered.price);
         }
         builder.clear();
      }
      long finish = System.currentTimeMillis();
      long duration = finish - start;
      
      System.err.println("Time taken for BIG " + duration + " ms which is " + format.format(ITERATIONS / (duration / 1000)) + " per second");
   }

}
