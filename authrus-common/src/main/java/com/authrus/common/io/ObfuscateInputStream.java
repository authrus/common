package com.authrus.common.io;

import java.io.IOException;
import java.io.InputStream;

public class ObfuscateInputStream extends InputStream {
   
   private InputStream source;
   private byte[] buffer;
   private byte[] mask;
   private byte[] swap;
   private int total;
   private int count;
   private int chunk;
   
   public ObfuscateInputStream(InputStream source, byte[] mask) {
      this.buffer = new byte[1024];
      this.swap = new byte[1];
      this.source = source;
      this.mask = mask;
   }

   @Override
   public int read() throws IOException {
      int count = read(swap);
      
      if(count != -1) {
         return swap[0] & 0xff;
      }
      return count;
   }
   
   @Override
   public int read(byte[] octets) throws IOException {
      return read(octets, 0, octets.length);
   }
   
   @Override
   public int read(byte[] octets, int offset, int length) throws IOException {
      int count = 0;
      
      if(chunk == 0) {
         if(total == 0) {
            int magic = read(2);
            int version = read(4);

            if(magic != 0xaced) {
               throw new IOException("Magic number expected at start of stream");
            }
            if(version != 0) {
               throw new IOException("Stream written in a later version " + version);
            }
         }
         int size = read(4);
         
         if(size == 0) { /* final block */
            int trailer = read(4);
            
            if(trailer != total) {
               throw new IOException("Total expected was " + trailer + " but is " + total);
            }
            return -1;
         }
         chunk = size;
      }   
      if(chunk != -1) {
         int space = Math.min(buffer.length, length); // read only what can be used
         int block = Math.min(space, chunk); // do not read past chunk
         int ready = block;
         
         while(ready > 0) {
            int size = source.read(buffer, 0, ready); // keep reading until we finish block 
            
            if(size == -1) {
               throw new IOException("Stream ended before chunk could be read");
            }
            for(int i = 0; i < size; i++) {
               octets[count++ + offset] = (byte)(buffer[i] ^ mask[total++ % mask.length]);           
            }
            chunk -= size;
            ready -= size;         
         }
         if(chunk <= 0) {
            int magic = read(2);
            
            if(magic != 0xaced) {
               throw new IOException("Magic number expected at end of chunk");
            }          
         }
         return block;
      }
      return -1;
   }
   
   private int read(int octets) throws IOException {
      int value = 0;
      
      for (int i = 0; i < octets; i++) {
         int octet = source.read();
         
         if(octet == -1) {
            return -1;
         }
         value <<= 8;
         value |= octet & 0xff;
      }
      return value;
   }
   
   @Override
   public int available() throws IOException {
      if(chunk > 0) {
         return chunk - count;
      }
      return 0;
   }

   @Override
   public void close() throws IOException {
      source.close();
   }

}
