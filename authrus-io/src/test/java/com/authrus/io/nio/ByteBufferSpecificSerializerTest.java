package com.authrus.io.nio;

import junit.framework.TestCase;

public class ByteBufferSpecificSerializerTest extends TestCase {

   public void testByteBuffers() throws Exception {
      ByteBufferSpecificSerializer serializer = new ByteBufferSpecificSerializer();
      Media media = new Media("copyright", "format", Media.Player.JAVA, "title", "uri", 1L, 2L, 3, 4, 5);       
      MediaContent content = new MediaContent(media);      
      long start = System.currentTimeMillis();
      
      for(int i = 0; i < 10000000; i++) {
         byte[] data = serializer.serialize(content);
         serializer.deserialize(data);
      }
      long end = System.currentTimeMillis();
      System.err.println(end - start);
   }

}
