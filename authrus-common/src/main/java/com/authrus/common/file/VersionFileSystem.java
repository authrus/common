package com.authrus.common.file;

import java.io.File;

import com.authrus.common.time.TimeStampBuilder;

public class VersionFileSystem {

   private final TimeStampBuilder builder;
   private final File directory;
   private final long duration;

   public VersionFileSystem(File directory) {
      this(directory, 86400000);
   }
   
   public VersionFileSystem(File directory, long duration) {
      this.builder = new TimeStampBuilder();
      this.directory = directory;
      this.duration = duration;
   }   
   
   public File createFile(String name) {
      String time = builder.createTimeStamp();
      String token = time.toLowerCase();     
      
      cleanFiles();
      
      return new File(directory, name + "." + token);
   }   
   
   private void cleanFiles() {     
      if(!directory.exists()) {
         directory.mkdirs();
      }
      scanDirectory();
   }
   
   private void scanDirectory() {
      File[] list = directory.listFiles();

      if(list != null) {
         for(File child : list) {
            long timeRemaining = timeRemaining(child);
            
            if(timeRemaining < 0) {
               purgeFile(child);
            }
         }
      }
   }      

   private void purgeFile(File file) {
      if(file.isDirectory()) {
         File[] list = file.listFiles();
         
         for(File child : list) {
            long timeRemaining = timeRemaining(child);
            
            if(timeRemaining < 0) {
               purgeFile(child);
            }
         }
      }
      file.delete();
   } 
   
   private long timeRemaining(File file) {
      long currentTime = System.currentTimeMillis();
      long lastModified = file.lastModified();
      long fileAge = currentTime - lastModified;
      
      return duration - fileAge;
   }
}
