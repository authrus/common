package com.authrus.tuple.frame;

import static java.nio.ByteOrder.*;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.transport.ByteCursor;

class FrameHeaderConsumer implements FrameHeader {

   private AtomicInteger sequence;
   private ByteOrder order;
   private FrameType type;
   private byte[] buffer;  
   private int[] header;
   private int version;
   private int length;
   private int count;

   public FrameHeaderConsumer() {
      this.sequence = new AtomicInteger();
      this.buffer = new byte[64];
      this.header = new int[3];
   }   
   
   @Override
   public int getVersion() {
      return version;
   }
   
   @Override
   public ByteOrder getOrder() {
      return order;
   }

   @Override
   public FrameType getType() {    
      return type;
   }    
   
   @Override
   public int getProlog() {
      return header[0];
   }

   @Override
   public int getSequence() {
      return header[1];
   }
   
   @Override
   public int getLength() {
      return header[2];
   }

   public void consume(ByteCursor cursor) throws IOException {
      if (cursor.isReady()) {
         if(count < 4) { 
            int required = 4 - count; /* 4 byte header prolog */
            int size = cursor.read(buffer, count, required);
            
            if(required - size == 0) {
               int protocol = buffer[0] & 0xff;
               int format = buffer[1] & 0xff;
               int code =  buffer[2] & 0xff;

               if(protocol > 1) {
                  throw new IOException("Protocol version " + protocol + " is not supported");
               }
               if(format == 0) {
                  order = LITTLE_ENDIAN;
               } else if(format == 1) {
                  order = BIG_ENDIAN;
               } else {
                  throw new IOException("Byte order " + format + " is not known");
               }
               type = FrameType.resolveType(code);
               length = 12; /* version 1 has 12 bytes total in header */               
            }
            count += size;
         }
         if(length > 0) {
            int required = length - count;
            int size = cursor.read(buffer, count, required);
            
            if (size == -1) {
               throw new EOFException("Could only read " + count + " bytes of header");
            }
            if(size >= required) {
               int seek = 0;
   
               for (int i = 0; i < header.length; i++) {
                  for (int j = 0; j < 4; j++) {
                     header[i] <<= 8;
                     header[i] |= buffer[seek++] & 0xff;
                  }
               }
               int expect = sequence.getAndIncrement();
               int order = header[1];
   
               if (order != expect) {
                  throw new IllegalStateException("Received frame " + order + " but expected " + expect);
               }              
            }
            count += size;
         }
      }
   }

   public boolean isFinished() {
      if(length > 0) {
         return count >= length;
      }
      return false;
   }

   public void clear() {      
      type = null;
      order = null;
      version = -1;
      length = 0;
      count = 0;
   }
}
