package com.authrus.tuple.frame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.authrus.io.ByteBufferReader;
import com.authrus.io.DataReader;

public class Frame {

   private final ByteBuffer buffer;
   private final FrameType type;  
   private final int length;

   public Frame(FrameType type) {
      this(type, ByteBuffer.allocate(0));
   }
   
   public Frame(FrameType type, ByteBuffer buffer) {
      this.length = buffer.remaining();
      this.buffer = buffer;
      this.type = type;
   }
   
   public ByteBuffer getData() {
      ByteBuffer copy = buffer.asReadOnlyBuffer();
      ByteOrder order = buffer.order();
      
      copy.order(order);
      
      return copy;
   }
   
   public DataReader getReader() {
      ByteBuffer copy = buffer.asReadOnlyBuffer();
      ByteOrder order = buffer.order();
      
      copy.order(order);
      
      return new ByteBufferReader(copy);
   }   

   public FrameType getType() {
      return type;
   }

   public int getSize() {
      return length;
   }
   
   @Override
   public String toString() {
      return type + " " + length;
   }
}
