package com.authrus.rest.content;

import java.io.File;

public class FileContentManager {
   
   private static final String INDEX_FILE = "index.html";
   private static final String[] SITE_PREFIXES = {
      "static",
      "META-INF/resources/webjars"
   };
   
   private final FileSystemLocator fileSystem;
   private final ClassPathLocator classPath;
   
   public FileContentManager(File base) {
      this.classPath = new ClassPathLocator(INDEX_FILE, SITE_PREFIXES);
      this.fileSystem = new FileSystemLocator(base);
   }
   
   public FileContent getContent(String path) {
      FileContent content = classPath.findFile(path);
      
      if(content == null) {
         return fileSystem.findFile(path);
      }
      return content;
   }

}
