package com.authrus.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class DataEncoderTest extends TestCase {   
   
   public void testStream() throws Exception {
      DataFormatter encoder = new DataFormatter();
      ByteArrayOutputStream output = new ByteArrayOutputStream();     
      DataWriter writer = new OutputStreamWriter(output);
      
      encoder.write(writer, "hello world");
      encoder.write(writer, "hello world");
      encoder.write(writer, 10);
      encoder.write(writer, 99L);      
      
      byte[] result = output.toByteArray();
      ByteArrayInputStream input = new ByteArrayInputStream(result);
      DataReader reader = new InputStreamReader(input);
      DataFormatter decoder = new DataFormatter();
      
      assertEquals(decoder.read(reader), "hello world");
      assertEquals(decoder.read(reader), "hello world");
      assertEquals(decoder.read(reader), 10);
      assertEquals(decoder.read(reader), 99L);
   }
   
   public void testNIO() throws Exception {
      DataFormatter encoder = new DataFormatter();
      ByteBufferBuilder buffer = new ByteBufferBuilder();
      DataWriter writer = new ByteBufferWriter(buffer);
      
      encoder.write(writer, "hello world");
      encoder.write(writer, "hello world");
      encoder.write(writer, 10);
      encoder.write(writer, 99L);
      
      ByteBuffer result = buffer.extract();
      DataReader reader = new ByteBufferReader(result);
      DataFormatter decoder = new DataFormatter();
      
      assertEquals(decoder.read(reader), "hello world");
      assertEquals(decoder.read(reader), "hello world");
      assertEquals(decoder.read(reader), 10);
      assertEquals(decoder.read(reader), 99L);
   }

}
