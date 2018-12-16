package com.authrus.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWriter implements DataWriter {
   
   private DataOutputStream stream;
   private char[] text;
   
   public OutputStreamWriter(OutputStream stream) {
      this.stream = new DataOutputStream(stream);
      this.text = new char[0];
   }

   @Override
   public void writeInt(int value) throws IOException {
      stream.writeInt(value);
   }

   @Override
   public void writeLong(long value) throws IOException {
      stream.writeLong(value); 
   }

   @Override
   public void writeByte(byte value) throws IOException {
      stream.writeByte(value); 
   }

   @Override
   public void writeShort(short value) throws IOException {
      stream.writeShort(value);   
   }

   @Override
   public void writeString(String value) throws IOException {
      int length = value.length();
      
      if(length > text.length) {
         text = new char[length];
      }
      value.getChars(0, length, text, 0);
      stream.writeInt(length);
      
      for(int i = 0; i < length; i++) {
         stream.writeChar(text[i]);
      }      
   }

   @Override
   public void writeChar(char value) throws IOException {
      stream.writeChar(value);
   }

   @Override
   public void writeBoolean(boolean value) throws IOException {
      stream.writeBoolean(value);
   }

   @Override
   public void writeFloat(float value) throws IOException {
      stream.writeFloat(value);
   }

   @Override
   public void writeDouble(double value) throws IOException {
      stream.writeDouble(value);
   }

}
