package com.authrus.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamReader implements DataReader {
   
   private DataInputStream stream;
   private char[] text;
   
   public InputStreamReader(InputStream stream) {
      this.stream = new DataInputStream(stream);
      this.text = new char[0];
   }

   @Override
   public int readInt() throws IOException {
      return stream.readInt();
   }

   @Override
   public long readLong() throws IOException {
      return stream.readLong();
   }

   @Override
   public byte readByte() throws IOException {
      return stream.readByte();
   }

   @Override
   public short readShort() throws IOException {
      return stream.readShort();
   }

   @Override
   public String readString() throws IOException {
      int length = stream.readInt();      
      
      if(length > text.length) {
         text = new char[length];
      }
      for(int i = 0; i < length; i++) {
         text[i] = stream.readChar();
      }
      return new String(text, 0, length);              
   }

   @Override
   public char readChar() throws IOException {
      return stream.readChar();
   }

   @Override
   public boolean readBoolean() throws IOException {
      return stream.readBoolean();
   }

   @Override
   public float readFloat() throws IOException {
      return stream.readFloat();
   }

   @Override
   public double readDouble() throws IOException {
      return stream.readDouble();
   }
   
   public void close() throws IOException {
      stream.close();
   }

}
