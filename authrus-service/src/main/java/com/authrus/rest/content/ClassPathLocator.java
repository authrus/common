package com.authrus.rest.content;

import com.authrus.common.ClassPathReader;
import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;

import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassPathLocator {

   private final Cache<String, String> failures;
   private final String[] prefixes;
   private final String index;
   
   public ClassPathLocator(String index, String... prefixes) {
      this.failures = new LeastRecentlyUsedCache<String, String>(100);
      this.prefixes = prefixes;
      this.index = index;
   }
   
   public FileContent findFile(String path) {
      try {
         String failure = failures.fetch(path);
         
         if(failure == null) {
            for(String prefix : prefixes) {
               FileContent content = findFiles(
                  String.format("/%s%s/%s", prefix, path, index),
                  String.format("/%s%s%s", prefix, path, index),
                  String.format("/%s%s", prefix, path)
               );
               
               if(content != null) {
                  return content;
               }
            }
            failures.cache(path, path);
         }
      }catch(Exception e) {
         log.info("Could not find file for "+ path, e);
      }
      return null;
   }
   
   private FileContent findFiles(String... paths) {
      for(String path : paths) {
         URL resource = ClassPathReader.getURL(path);
         
         if(resource != null) {
            boolean directory = path.endsWith("/");
            
            if(!directory) {
               return FileContent.builder()
                  .path(path)
                  .resource(resource)
                  .build();
            }
         }
      }
      return null;
   }
}
