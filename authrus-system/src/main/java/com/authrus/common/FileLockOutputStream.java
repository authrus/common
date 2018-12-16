package com.authrus.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileLockOutputStream extends OutputStream {
   
   private final AtomicBoolean closed;
   private final OutputStream output;
   private final File from;
   private final File to;

   public FileLockOutputStream(File from, File to) throws Exception {
      this.output = new FileOutputStream(from);
      this.closed = new AtomicBoolean();
      this.from = from;
      this.to = to;
   }
   
   @Override
   public void write(int octet) throws IOException { 
      output.write(octet);
   }
   
   @Override
   public void write(byte[] source, int off, int len) throws IOException {
      output.write(source, off, len);
   }
   
   @Override
   public void flush() throws IOException {
      output.flush();
   }
   
   @Override
   public void close() throws IOException {
      try {
         if(!closed.getAndSet(true)) {
            output.close();
            
            for(int i = 0; i < 10; i++) {
               if(to.exists()) {
                  to.delete();
               } else {
                  from.renameTo(to);
                  break;
               }         
            }
         }
      } finally {
         if(from.exists()) {
            from.delete();
         }
      }
   }

}
