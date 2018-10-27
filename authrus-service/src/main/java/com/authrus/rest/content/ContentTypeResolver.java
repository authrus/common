package com.authrus.rest.content;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;

@Slf4j
public class ContentTypeResolver {

   private final Tika tika;
   
   public ContentTypeResolver() {
      this.tika = new Tika();
   }
   
   public String resolveType(String path) {
      try {
         return tika.detect(path);
      } catch(Exception e) {
         log.info("Could not resolve type for " + path, e);
      }
      return APPLICATION_OCTET_STREAM;
   }
}
