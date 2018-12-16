package com.authrus.io;

public class ByteBufferWriter implements DataWriter {

   protected ByteBufferBuilder builder;
   protected char[] text;
   
   public ByteBufferWriter(ByteBufferBuilder builder) {
      this.text = new char[0];
      this.builder = builder;
   }
   
   public void writeInt(int value) {
      builder.append(value);
   }
   
   public void writeLong(long value) {
      builder.append(value);
   }
   
   public void writeByte(byte value) {
      builder.append(value);
   }
   
   public void writeShort(short value) {
      builder.append(value);
   }
   
   public void writeString(String value) {
      int length = value.length();
      
      if(length > text.length) {
         text = new char[length];
      }
      value.getChars(0, length, text, 0);
      builder.append(length);
      builder.append(text, 0, length);   
   }
   
   public void writeChar(char value) {
      builder.append(value);
   }
   
   public void writeBoolean(boolean value) {
      builder.append(value);
   }
   
   public void writeFloat(float value) {
      builder.append(value);
   }
   
   public void writeDouble(double value) {
      builder.append(value);    
   }
}
