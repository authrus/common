package com.authrus.rest.content;

import java.io.File;
import java.net.URI;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSystemLocator {

   private final File base;
   
   public FileSystemLocator(File base) {
      this.base = base;
   }
   
   public FileContent findFile(String path) {
      try {
         File file = new File(base, path);
         
         if(file.exists()) {
            URI location = file.toURI();
            URL resource = location.toURL();
            boolean directory = file.isDirectory();
            
            return FileContent.builder()
                  .directory(directory)
                  .path(path)
                  .file(file)
                  .resource(resource)
                  .build();
         }
         return null;
      }catch(Exception e) {
         log.info("Could not find file for " + path, e);
      }
      return null;
   }
}
