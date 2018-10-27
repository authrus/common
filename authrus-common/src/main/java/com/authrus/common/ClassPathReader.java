package com.authrus.common;

import java.io.InputStream;
import java.net.URL;

import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;

public class ClassPathReader {

   public static String getPath(String resource) {
      URL url = getURL(resource);
      
      if(url != null) {
         return url.toExternalForm();
      }
      return null;
   }
   
   public static URL getURL(String resource) {
      URL url = ClassPathReader.class.getResource(resource);
      
      if(url == null) {
         return ClassPathReader.class.getResource("/" + resource);
      }
      return url;
   }
   
   public static InputStream getInputStream(String resource) {
      InputStream stream = ClassPathReader.class.getResourceAsStream(resource);
      
      if(stream == null) {
         return ClassPathReader.class.getResourceAsStream("/" + resource);
      }
      return stream;
   }
   
   @SneakyThrows
   public static byte[] getBytes(String resource) {
      InputStream stream = getInputStream(resource);
      
      if(stream != null) {
         return IOUtils.toByteArray(stream);
      }
      return null;
   }
}
