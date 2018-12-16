package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameType.RECEIPT;

import java.nio.ByteBuffer;

public class Sequence {
   
   private final long sequence;

   public Sequence(long sequence) {
      this.sequence = sequence;
   }   
   
   public long getSequence() {
      return sequence;
   }
   
   public Frame getFrame() {
      ByteBuffer data = getData();
      
      if(data != null) {
         return new Frame(RECEIPT, data);
      }
      return null;
   }
   
   public ByteBuffer getData() {
      byte[] data = new byte[4];
      
      data[0] = (byte) ((sequence >>> 24) & 0xff); 
      data[1] = (byte) ((sequence >>> 16) & 0xff);
      data[2] = (byte) ((sequence >>> 8) & 0xff);
      data[3] = (byte) ((sequence) & 0xff); 
      
      return ByteBuffer.wrap(data);
   }
   
   @Override
   public String toString() {
      return String.valueOf(sequence);
   }
}
