package com.authrus.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

public class ByteBufferBuilder {
   
   private ByteBuffer buffer;
   
   public ByteBufferBuilder() {
      this(20000);
   }
   
   public ByteBufferBuilder(int capacity) {
      this.buffer = ByteBuffer.allocateDirect(capacity);
   }
   
   public void order(ByteOrder order) {
      buffer.order(order);
   }

   public void append(int value) {
      int remaining = buffer.remaining();
      
      if(remaining < 4) {
         expand(4);         
      }
      buffer.putInt(value);
   }
   
   public void append(long value) {
      int remaining = buffer.remaining();
      
      if(remaining < 8) {
         expand(8);         
      }
      buffer.putLong(value);
   }
   
   public void append(byte value) {
      int remaining = buffer.remaining();
      
      if(remaining < 1) {
         expand(1);         
      }
      buffer.put(value);
   }
   
   public void append(byte[] data) {
      append(data, 0, data.length);
   }
   
   public void append(byte[] data, int offset, int length) {
      int remaining = buffer.remaining();
      
      if(remaining < length) {
         expand(length);         
      }
      buffer.put(data, offset, length);
   }
   
   public void append(short value) {
      int remaining = buffer.remaining();
      
      if(remaining < 2) {
         expand(2);         
      }
      buffer.putShort(value);
   }

   public void append(char[] text) {
      append(text, 0, text.length);
   }
   
   public void append(char[] text, int offset, int length) {
      int remaining = buffer.remaining();
      int position = buffer.position();
      int require = length * 2;      
      
      if(remaining < require) {
         expand(require);         
      }
      CharBuffer encoder = buffer.asCharBuffer();
      
      encoder.put(text, offset, length);
      buffer.position(position + require); 
   }
   
   public void append(char value) {
      int remaining = buffer.remaining();
      
      if(remaining < 2) {
         expand(2);         
      }
      buffer.putChar(value);
   }
   
   public void append(boolean value) {
      int remaining = buffer.remaining();
      int octet = (value ? 1 : 0);
      
      if(remaining < 1) {
         expand(1);         
      }      
      buffer.put((byte)octet);
   }
   
   public void append(float value) {
      int remaining = buffer.remaining();
      
      if(remaining < 4) {
         expand(4);         
      }
      buffer.putFloat(value);
   }
   
   public void append(double value) {
      int remaining = buffer.remaining();
      
      if(remaining < 8) {
         expand(8);         
      }
      buffer.putDouble(value);      
   }
   
   private void expand(int require) {
      int space = buffer.remaining();
      int capacity = buffer.capacity();
      int size = Math.max(require, capacity * 2);
      
      if(size > space) {
         ByteBuffer copy = ByteBuffer.allocateDirect(size);     
         ByteOrder order = buffer.order();
         
         buffer.flip();
         copy.put(buffer);
         copy.order(order);
         buffer = copy;
      }
   }   
   
   public ByteBuffer extract() {
      ByteBuffer copy = buffer.asReadOnlyBuffer();
      ByteOrder order = buffer.order();
      
      copy.flip();
      copy.order(order);
      
      return copy;
   }
   
   public int length() {
      int remaining = buffer.remaining();
      int capacity = buffer.capacity();
      
      return capacity - remaining;
   }
   
   public void clear() {
      buffer.position(0);
   }
}
