package com.authrus.tuple.frame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.simpleframework.transport.ByteCursor;

class FrameConsumer {

   private FrameHeaderConsumer header;
   private byte[] buffer;
   private int limit;
   private int count;

   public FrameConsumer() {
      this(1048576);
   }
   
   public FrameConsumer(int limit) {
      this.header = new FrameHeaderConsumer();
      this.buffer = new byte[2048];
      this.limit = limit;
   }

   public FrameType getType() {
      return header.getType();
   }

   public Frame getFrame() {
      ByteOrder order = header.getOrder();
      FrameType type = header.getType();
      int length = header.getLength();
      
      if(type != null) {
         ByteBuffer data = ByteBuffer.wrap(buffer, 0, length);
         ByteBuffer copy = data.asReadOnlyBuffer();
         
         copy.order(order);
         
         return new Frame(type, copy);
      }
      return null;
   }
   
   public Sequence getSequence() {
      FrameType type = header.getType();
      long sequence = header.getSequence();
      
      if(type != null) {
         return new Sequence(sequence);
      }
      return null;
   }

   public void consume(ByteCursor cursor) throws IOException {
      while (cursor.isReady()) {
         if(!header.isFinished()) {
            header.consume(cursor);
         }
         if(header.isFinished()) {
            int length = header.getLength(); /* how big is the frame */
            
            if(count <= length) {            
               if(buffer.length < length) {
                  buffer = new byte[length]; /* if body is very big */
               }
               if(count < length) {
                  int read = cursor.read(buffer, count, length - count);
                  
                  if(read == -1) {
                     throw new IOException("Could only read " + count + " of length " + length);
                  }
                  count += read;
               }
               if(count == length) {    
                  break;
               }
            }
         }
      }
   }

   public boolean isFinished() {
      if(header.isFinished()) {
         int length = header.getLength();
         
         if(count == length) {
            return true;
         }
      }
      return false;
   }

   public void clear() {
      if(count > limit) {
         buffer = new byte[limit]; /* do not keep large buffers */
      }
      header.clear();
      count = 0;
   }
}
