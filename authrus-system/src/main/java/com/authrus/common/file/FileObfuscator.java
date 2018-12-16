package com.authrus.common.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.FileLockOutputStream;
import com.authrus.common.io.ObfuscateInputStream;
import com.authrus.common.io.ObfuscateOutputStream;

public class FileObfuscator {
   
   private static final Logger LOG = LoggerFactory.getLogger(FileObfuscator.class);

   private final File rootPath;
   private final boolean obfuscate;
   private final boolean debug;
   private final long expiry;
   
   public FileObfuscator(File rootPath, boolean debug) {
      this(rootPath, debug, 60 * 60 * 1000);
   }
   
   public FileObfuscator(File rootPath, boolean debug, long expiry) {
      this(rootPath, debug, expiry, true);
   }
   
   public FileObfuscator(File rootPath, boolean debug, long expiry, boolean obfuscate) {
      this.obfuscate = obfuscate;
      this.rootPath = rootPath;
      this.debug = debug;
      this.expiry = expiry;
   }  

   public OutputStream createFile(String fileName) {
      try {
         FileLocation fileInfo = createLocation(fileName);
         File realFile = fileInfo.getRealFile();
         File parentFile = realFile.getParentFile();
         File tempFile = fileInfo.getTempFile();
         byte[] mask = fileInfo.getMask();

         if(!parentFile.exists()) {
            parentFile.mkdirs();
         }
         OutputStream fileOutput = new FileLockOutputStream(tempFile, realFile);
         
         if(obfuscate) {
            fileOutput = new ObfuscateOutputStream(fileOutput, mask);
         }
         cleanFiles(parentFile);
         
         if(debug) {
            LOG.info("Opening file " + tempFile + " to write " + realFile + " atomically");
         }
         return fileOutput;
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
   }

   public InputStream openFile(String fileName) {
      try {
         FileLocation fileInfo = createLocation(fileName);
         File realFile = fileInfo.getRealFile();
         byte[] mask = fileInfo.getMask();
         InputStream fileInput = new FileInputStream(realFile);
         
         if(obfuscate) {
            fileInput = new ObfuscateInputStream(fileInput, mask);
         }
         if(debug) {
            LOG.info("Opening file " + realFile + " for " + fileName);
         }
         return new BufferedInputStream(fileInput, 4096);
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
   }
   
   public boolean containsFile(String fileName) {
      try {
         FileLocation fileInfo = createLocation(fileName);
         File realFile = fileInfo.getRealFile();

         return realFile.exists();
      } catch(Exception e) {
         throw new IllegalStateException("Could not check for file", e);
      }
   }
   
   public boolean deleteFile(String fileName) {
      try {
         FileLocation fileInfo = createLocation(fileName);
         File realFile = fileInfo.getRealFile();

         if(realFile.exists()) {
            for(int i = 0; i < 10; i++) {               
               if(realFile.delete()) {
                  return true;
               }
            }
         }
         return false;
      } catch(Exception e) {
         throw new IllegalStateException("Could not check for file", e);
      }
   }

   private FileLocation createLocation(String fileName) {
      try {
         File realFile = new File(rootPath, fileName);
         String realPath = realFile.getCanonicalPath();
         byte[] mask = realPath.getBytes("UTF-8"); // build from canonical path!
         long timeStamp = System.currentTimeMillis();
         int extensionIndex = fileName.lastIndexOf(".");
         String fileSuffix = fileName.substring(extensionIndex + 1);
         String filePrefix = fileName.substring(0, extensionIndex);
         File tempFile = new File(rootPath, filePrefix + "." + timeStamp + "." + fileSuffix + ".tmp");
        
         return new FileLocation(realFile, tempFile, mask);         
      } catch(Exception e) {
         throw new IllegalStateException("Could not create info for " + fileName);
      }
   }    
   
   private void cleanFiles(File directory) {
      File[] fileList = directory.listFiles();
      long currentTime = System.currentTimeMillis();
      long expiryTime = currentTime - expiry;
      
      for(File existingFile : fileList) {
         String name = existingFile.getName();
         long lastModified = existingFile.lastModified();            
         
         if(existingFile.isFile() && name.endsWith(".tmp")) {
            if(lastModified < expiryTime) {
               if(debug) {
                  LOG.info("Cleaning up temporary file " + existingFile + " that was not closed properly");
               }
               existingFile.delete();
            }
         }
      }
   }
   
   private static class FileLocation {
      
      private final File tempFile;
      private final File realFile;
      private final byte[] mask;
      
      public FileLocation(File realFile, File tempFile, byte[] mask) {
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
   
}
