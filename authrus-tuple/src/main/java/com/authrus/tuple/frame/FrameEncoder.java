package com.authrus.tuple.frame;

import static java.nio.ByteOrder.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;

public class FrameEncoder {

   private AtomicInteger sequence;
   private Channel channel;
   private byte[] header;

   public FrameEncoder(Channel channel) {
      this(channel, 8192);
   }
   
   public FrameEncoder(Channel channel, int capacity) {
      this.sequence = new AtomicInteger();
      this.header = new byte[12];
      this.channel = channel;
   }

   public synchronized int encode(Frame frame) throws IOException {
      ByteBuffer buffer = frame.getData();            
      ByteWriter writer = channel.getWriter();
      ByteOrder order = buffer.order();
      FrameType type = frame.getType();
      int count = sequence.getAndIncrement();
      int size = frame.getSize();
      int index = 0;
      
      header[index++] = (byte) 1; /* 256 possible versions */
      header[index++] = (byte) (order == LITTLE_ENDIAN ? 0 : 1); /* byte order */      
      header[index++] = (byte) (type.type); /* 256 possible frame types */      
      header[index++] = (byte) 0; /* reserved */        
      header[index++] = (byte) (count >>> 24); /* frame sequence */
      header[index++] = (byte) (count >>> 16);
      header[index++] = (byte) (count >>> 8);      
      header[index++] = (byte) (count);      
      header[index++] = (byte) (size >>> 24); /* data length */
      header[index++] = (byte) (size >>> 16);
      header[index++] = (byte) (size >>> 8);
      header[index++] = (byte) (size);          

      writer.write(header);
      writer.write(buffer);
      writer.flush();
      
      return count;
   }

   public synchronized void close() throws IOException {
      channel.close();
   }
}
