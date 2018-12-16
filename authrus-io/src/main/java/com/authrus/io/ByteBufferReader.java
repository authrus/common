package com.authrus.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class ByteBufferReader implements DataReader {
   
   protected ByteBuffer buffer;
   protected char[] text;
   
   public ByteBufferReader() {
      this(ByteBuffer.allocate(0));
   }
   
   public ByteBufferReader(ByteBuffer buffer) {
      this.text = new char[0];
      this.buffer = buffer;
   }
   
   @Override
   public int readInt() {
      return buffer.getInt();
   }   
  
   @Override
   public long readLong() {
      return buffer.getLong();
   }
   
   @Override
   public byte readByte() {
      return buffer.get();
   }
   
   @Override
   public short readShort() {
      return buffer.getShort();
   }
   
   @Override
   public String readString() {
      int length = buffer.getInt();      
      CharBuffer decoder = buffer.asCharBuffer();
      
      if(length > 0) {
         int position = buffer.position();
         
         if(text.length < length) {
            text = new char[length];
         }
         decoder.get(text, 0, length);
         buffer.position(position + length * 2);
      }
      return new String(text, 0, length);
   }
   
   @Override
   public char readChar() {
      return buffer.getChar();
   }   
   
   @Override
   public boolean readBoolean() {
      return buffer.get() == 1; 
   }   
   
   @Override
   public float readFloat() {
      return buffer.getFloat();
   }
   
   @Override
   public double readDouble() {
      return buffer.getDouble();
   }
}
