package com.authrus.common.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import junit.framework.TestCase;

public class ObfuscateStreamTest extends TestCase {
   
   public void testOnFileSystem() throws IOException {
      File file = new File("c:\\temp");
      FS fs = new FS(file);
      OutputStream out = fs.createExternalFile("test.png");
      byte[] data = new byte[1024 * 90];
      
      int remaining = data.length;     
      Random rand = new Random();
      for(int i = 0; i < data.length;i++){
         data[i] = (byte)rand.nextInt(256);
      }
      while(remaining > 0){
         int size =rand.nextInt(4096);
         int min = Math.min(size, remaining);
         out.write(data, data.length-remaining, min);
         remaining-=min;
      }
      out.close();
      InputStream in = fs.openExternalFile("test.png");
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] block = new byte[4096];
      int count = 0;
      while((count = in.read(block)) !=-1){
         buffer.write(block,0,count);
      }
      in.close();
      byte[]result = buffer.toByteArray();
      
      assertEquals(result.length, data.length);
      for(int i = 0; i < data.length;i++){
         assertEquals("At index " + i + " of " + data.length, result[i],data[i]);
      }
      
   }
   
   public void testStreams() throws IOException {
      ByteArrayOutputStream muddy = new ByteArrayOutputStream();
      ObfuscateOutputStream out = new ObfuscateOutputStream(muddy, "test".getBytes());
      ByteArrayOutputStream clear = new ByteArrayOutputStream();
      
      out.write("Hello World".getBytes());
      out.write("This is a test".getBytes());
      out.write("Blah blah blah".getBytes());
      out.write("More stuff".getBytes());
      out.close();      
      
      clear.write("Hello World".getBytes());
      clear.write("This is a test".getBytes());
      clear.write("Blah blah blah".getBytes());
      clear.write("More stuff".getBytes());
      clear.close();
      
      byte[] data = muddy.toByteArray();
      ByteArrayInputStream source = new ByteArrayInputStream(data);
      ObfuscateInputStream in = new ObfuscateInputStream(source, "test".getBytes());
      ByteArrayOutputStream verify = new ByteArrayOutputStream();
      byte[] swap = new byte[100];
      int count = 0;
      
      while((count = in.read(swap)) != -1) {
         verify.write(swap, 0, count);
      }
      byte[] recovered = verify.toByteArray();
      byte[] original = clear.toByteArray();
      
      assertEquals(recovered.length, original.length);
      
      for(int i = 0; i < recovered.length; i++) {
         assertEquals(recovered[i], original[i]);
      }      
   }
   
   private static class FS {
      
      private final File rootPath;
      
      public FS(File rootPath) {
         this.rootPath = rootPath;
      }

      private FileInfo createExternalInfo(String fileName) {
         try {
            File realFile = new File(rootPath, fileName);
            String realPath = realFile.getCanonicalPath();
            byte[] mask = realPath.getBytes("UTF-8"); // build from canonical path!
            long timeStamp = System.currentTimeMillis();
            int extensionIndex = fileName.lastIndexOf(".");
            String fileSuffix = fileName.substring(extensionIndex + 1);
            String filePrefix = fileName.substring(0, extensionIndex);
            String tempName = String.format("%s.%s.%s.tmp", filePrefix, timeStamp, fileSuffix);
            File tempFile = new File(rootPath, tempName);
           
            return new FileInfo(realFile, tempFile, mask);         
         } catch(Exception e) {
            throw new IllegalStateException("Could not create info for " + fileName);
         }
      }   
   
      public OutputStream createExternalFile(String fileName) {
         try {
            FileInfo fileInfo = createExternalInfo(fileName);
            File realFile = fileInfo.getRealFile();
            File parentFile = realFile.getParentFile();
            File tempFile = fileInfo.getTempFile();
            byte[] mask = fileInfo.getMask();
   
            if(!parentFile.exists()) {
               parentFile.mkdirs();
            }
            FileLockOutputStream fileOutput = new FileLockOutputStream(tempFile, realFile);
            ObfuscateOutputStream maskOutput = new ObfuscateOutputStream(fileOutput, mask);
            File[] fileList = parentFile.listFiles();
            long currentTime = System.currentTimeMillis();
            long expiryTime = currentTime - (60 * 60 * 1000);
            
            for(File existingFile : fileList) {
               String name = existingFile.getName();
               long lastModified = existingFile.lastModified();            
               
               if(existingFile.isFile() && name.endsWith(".tmp")) {
                  if(lastModified < expiryTime) {
                     System.err.println("Cleaning up temporary file " + existingFile + " that was not closed properly");
                     existingFile.delete();
                  }
               }
            }
            System.err.println("Opening file " + tempFile + " to write " + realFile + " atomically");
            return maskOutput;
         } catch(Exception e) {
            throw new IllegalStateException("Could not open stream", e);
         }
      }
   
      public InputStream openExternalFile(String fileName) {
         try {
            FileInfo fileInfo = createExternalInfo(fileName);
            File realFile = fileInfo.getRealFile();
            byte[] mask = fileInfo.getMask();
            FileInputStream fileInput = new FileInputStream(realFile);
            ObfuscateInputStream maskInput = new ObfuscateInputStream(fileInput, mask);
            BufferedInputStream bufferedInput = new BufferedInputStream(maskInput, 4096);
            
            System.err.println("Opening file " + realFile + " for " + fileName);
            
            return bufferedInput;
         } catch(Exception e) {
            throw new IllegalStateException("Could not open stream", e);
         }
      }
   }
   
   private static class FileInfo {
      
      private final File tempFile;
      private final File realFile;
      private final byte[] mask;
      
      public FileInfo(File realFile, File tempFile, byte[] mask) {
         this.tempFile = tempFile;
         this.realFile = realFile;
         this.mask = mask;
      }
      
      public File getTempFile(){
         return tempFile;
      }
      
      public File getRealFile(){
         return realFile;
      }
      
      public byte[] getMask() {
         return mask;
      }
   }
   
   private static class FileLockOutputStream extends OutputStream {
      
      private final OutputStream output;
      private final File from;
      private final File to;

      public FileLockOutputStream(File from, File to) throws Exception {
         this.output = new FileOutputStream(from);
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
            output.close();
            
            for(int i = 0; i < 10; i++) {
               if(to.exists()) {
                  to.delete();
               } else {
                  from.renameTo(to);
                  break;
               }         
            }
         } finally {
            if(from.exists()) {
               from.delete();
            }
         }
      }

   }
}
