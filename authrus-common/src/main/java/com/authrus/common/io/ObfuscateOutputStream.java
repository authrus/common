package com.authrus.common.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ObfuscateOutputStream extends OutputStream {
   
   private OutputStream out;
   private byte[] buffer;
   private byte[] mask;
   private byte[] swap;
   private int version;
   private int total;

   public ObfuscateOutputStream(OutputStream out, byte[] mask) {
      this(out, mask, 0);
   }
   
   public ObfuscateOutputStream(OutputStream out, byte[] mask, int version) {
      this.out = new BufferedOutputStream(out, 4096);
      this.buffer = new byte[1024];
      this.swap = new byte[1];
      this.version = version;     
      this.mask = mask;
   }

   @Override
   public void write(int octet) throws IOException {
      swap[0] = (byte)octet;
      write(swap, 0, 1);
   }
   
   @Override
   public void write(byte[] octets) throws IOException {
      write(octets, 0, octets.length);
   }
   
   @Override
   public void write(byte[] octets, int offset, int length) throws IOException {
      int blocks = length / buffer.length; 
      int count = 0;
      
      if(length > 0) {
         if(total == 0) {
            out.write(0xac); /* magic number to start the stream */
            out.write(0xed);
            out.write((version >>> 24) & 0xff); /* version */ 
            out.write((version >>> 16) & 0xff);
            out.write((version >>> 8) & 0xff);
            out.write((version) & 0xff);   
         }
         out.write((length >>> 24) & 0xff); /* header length */ 
         out.write((length >>> 16) & 0xff);
         out.write((length >>> 8) & 0xff);
         out.write((length) & 0xff);    
   
         for(int i = 0; i < blocks; i++) {
            int chunk = Math.min(length, buffer.length);
         
            for(int j = 0; j < chunk; j++) {
               buffer[j] = (byte)(octets[count++ + offset] ^ mask[total++ % mask.length]);
            }
            out.write(buffer, 0, chunk);
         }
         if(count < length) {
            int chunk = length - count;
            
            for(int i = 0; i < chunk; i++) {
               buffer[i] = (byte)(octets[count++ + offset] ^ mask[total++ % mask.length]);
            }
            out.write(buffer, 0, chunk);
         }
         out.write(0xac); /* magic number to end the chunk */
         out.write(0xed);
      }
   }
   
   @Override
   public void flush() throws IOException {
      out.flush();
   }
   
   @Override
   public void close() throws IOException {
      out.write(0); /* zero chunk */ 
      out.write(0);
      out.write(0);
      out.write(0);
      out.write((total >>> 24) & 0xff); /* total length */ 
      out.write((total >>> 16) & 0xff);
      out.write((total >>> 8) & 0xff);
      out.write((total) & 0xff);
      out.flush();
      out.close();
   }
}
