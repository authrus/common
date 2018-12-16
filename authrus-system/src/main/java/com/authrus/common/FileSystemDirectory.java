package com.authrus.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.file.FileObfuscator;

public class FileSystemDirectory implements FileSystem {
   
   private static final Logger LOG = LoggerFactory.getLogger(FileSystemDirectory.class);

   private final FileObfuscator fileObfuscator;
   private final File externalPath;
   private final File rootPath;

   public FileSystemDirectory(String rootPath, String workPath) {
      this(rootPath, workPath, true);
   }
   
   public FileSystemDirectory(String rootPath, String workPath, boolean obfuscate) {
      this.rootPath = new File(rootPath);
      this.externalPath = new File(rootPath, workPath);
      this.fileObfuscator = new FileObfuscator(externalPath, true, 3600000, obfuscate);
   }

   public long freeExternalSpace() {
      return new File("/").getFreeSpace();
   }

   public long lastModified(String file) {
      return new File(rootPath, file).lastModified();
   }

   public long lastExternalModified(String file) {
      File outputFile = new File(externalPath, file);
      return outputFile.lastModified();
   }
   
   public long sizeOfFile(String fileName) {
      File file = new File(rootPath, fileName);
      
      if(file.exists()) {
         return file.length();
      }
      return 0;
   }

   public long sizeOfExternalFile(String fileName) {
      File file = new File(externalPath, fileName);

      if(file.exists()) {
         long size = file.length();
         
         if(file.isDirectory()) {
            File[] files = file.listFiles();
            
            for(File child : files) {
               String childName = child.getName();               
               
               if(child.isDirectory()) {
                  if(fileName.endsWith("/")) {
                     size += sizeOfExternalFile(fileName + childName);
                  } else {
                     size += sizeOfExternalFile(fileName + "/" + childName);
                  }
               } else {
                  size += child.length();
               }
            }
         }
         return size;         
      }
      return 0;
   }

   public boolean touchExternalFile(String fileName) {
      File outputFile = new File(externalPath, fileName);
      File parentFile = outputFile.getParentFile();

      if(!parentFile.exists()) {
         parentFile.mkdirs();
      }
      if(!outputFile.exists()) {
         try {
            outputFile.createNewFile();
         } catch(Exception e) {
            throw new IllegalStateException("Could not touch file " + fileName, e);
         }
      }
      long currentTime = System.currentTimeMillis();
      return outputFile.setLastModified(currentTime);
   }

   @Override
   public OutputStream createExternalFile(String fileName) {
      try {
         return fileObfuscator.createFile(fileName);
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
   }

   @Override
   public InputStream openExternalFile(String fileName) {
      try {
         return fileObfuscator.openFile(fileName);
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
   }
   
   @Override
   public byte[] loadExternalFile(String fileName) {
      try {
         InputStream source = openExternalFile(fileName);
   
         if(source != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;         
            
            try {
               while((count = source.read(chunk)) != -1) {
                  buffer.write(chunk, 0, count);
               }
            } finally {
               source.close();
            }
            return buffer.toByteArray();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
      return new byte[0];
   }

   @Override
   public InputStream openFile(String fileName) {
      try {
         if(fileName.startsWith("http://")) {
            URL file = new URL(fileName);

            LOG.info("Opening URL " + file);
            return file.openStream();
         }
         File file = new File(rootPath, fileName);

         LOG.trace("Opening file " + file);
         return new FileInputStream(file);
      } catch(Exception e) {
         throw new IllegalStateException("Could not open input stream for " + fileName, e);
      }
   }
   
   @Override
   public byte[] loadFile(String fileName) {
      try {
         InputStream source = openFile(fileName);
   
         if(source != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int count = 0;         
            
            try {
               while((count = source.read(chunk)) != -1) {
                  buffer.write(chunk, 0, count);
               }
            } finally {
               source.close();
            }
            return buffer.toByteArray();
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not open stream", e);
      }
      return new byte[0];
   }

   @Override
   public String[] listExternalFiles(String filter) {
      File file = new File(externalPath, filter);
      return listFiles(file, externalPath, filter);
   }

   @Override
   public String[] listFiles(String filter) {
      File file = new File(rootPath, filter);
      return listFiles(file, rootPath, filter);
   }

   private String[] listFiles(File file, File relativeTo, String filter) {
      if(file.exists()) {
         if(file.isDirectory()) {
            return file.list();
         } else {
            return new String[]{filter};
         }
      } else {
         File directory = file.getParentFile();

         if(directory.exists()) {
            List<String> matches = new ArrayList<String>();
            String[] list = directory.list();
            String pattern = file.getName();
            String prefix = "";

            if(!directory.equals(relativeTo)) {
               String path = directory.getPath();
               String pathPrefix = relativeTo.getPath();

               if(path.startsWith(pathPrefix)) {
                  int lengthOfPrefix = pathPrefix.length();
                  int indexForStart = lengthOfPrefix + 1;

                  prefix = path.substring(indexForStart) + "/";
               }
            }
            for(String entry : list) {
               if(entry.matches(pattern)) {
                  matches.add(prefix + entry);
               }
            }
            return matches.toArray(new String[]{});
         }
      }
      return new String[]{};
   }

   @Override
   public boolean deleteExternalFile(String fileName) {
      File inputFile = new File(externalPath, fileName);

      if(inputFile.exists()) {
         if(inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            
            for(File child : files) {
               String childName = child.getName();               
               
               if(child.isDirectory()) {
                  if(fileName.endsWith("/")) {
                     deleteExternalFile(fileName + childName);
                  } else {
                     deleteExternalFile(fileName + "/" + childName);
                  }
               } else {
                  child.delete();
               }
            }
         }
         return inputFile.delete();
      }
      return false;
   }

   @Override
   public boolean deleteFile(String name) {
      File file = new File(rootPath, name);

      if(file.exists()) {
         return file.delete();
      }
      return false;
   }
}
