package com.authrus.io;

import java.nio.ByteBuffer;
import java.util.Random;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferReader;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataReader;
import com.authrus.io.DataWriter;

import junit.framework.TestCase;

public class DataBufferTest extends TestCase {   
   
   public void testManyReads() throws Exception {
      ByteBufferBuilder buffer = new ByteBufferBuilder();
      Random random = new Random();
      
      for(int i = 0; i < 100000; i++) {
         buffer.clear();
         DataWriter writer = new ByteBufferWriter(buffer);
         double doubleValue = random.nextDouble();
         float floatValue = random.nextFloat();
         int intValue = random.nextInt(1000);
         
         writer.writeBoolean(true);
         writer.writeString("This is a text string!!");
         writer.writeString("Another string!");
         writer.writeInt(12);
         writer.writeDouble(doubleValue);
         writer.writeFloat(floatValue);
         writer.writeInt(intValue);
         writer.writeChar('T');
         writer.writeChar('X');
         writer.writeBoolean(intValue % 2 == 0);
         writer.writeShort((short)12);
         
         ByteBuffer result = buffer.extract();
         DataReader reader = new ByteBufferReader(result);
         
         assertEquals(reader.readBoolean(), true);
         assertEquals(reader.readString(), "This is a text string!!");
         assertEquals(reader.readString(), "Another string!");
         assertEquals(reader.readInt(), 12);         
         assertEquals(reader.readDouble(), doubleValue);
         assertEquals(reader.readFloat(), floatValue);
         assertEquals(reader.readInt(), intValue);
         assertEquals(reader.readChar(), 'T');
         assertEquals(reader.readChar(), 'X');
         assertEquals(reader.readBoolean(), intValue % 2 == 0);
         assertEquals(reader.readShort(), (short)12);
      }
   }   
   
   public void testStrings() throws Exception {
      ByteBufferBuilder buffer = new ByteBufferBuilder();
      DataWriter writer = new ByteBufferWriter(buffer);
      
      writer.writeBoolean(true);
      writer.writeString("This is a text string!!");
      writer.writeString("Another string!");
      writer.writeInt(12);
      
      ByteBuffer result = buffer.extract();
      DataReader reader = new ByteBufferReader(result);
      
      assertEquals(reader.readBoolean(), true);
      assertEquals(reader.readString(), "This is a text string!!");
      assertEquals(reader.readString(), "Another string!");
      assertEquals(reader.readInt(), 12);
   }
   
   public void testBuffer() throws Exception {
      ByteBufferBuilder buffer = new ByteBufferBuilder();
      buffer.append(true);
      buffer.append(false);
      buffer.append((byte)22);
      buffer.append(12);
      
      ByteBuffer result = buffer.extract();
      DataReader reader = new ByteBufferReader(result);
      
      assertEquals(reader.readBoolean(), true);
      assertEquals(reader.readBoolean(), false);
      assertEquals(reader.readByte(), (byte)22);
      assertEquals(reader.readInt(), 12);
      
   }

}
