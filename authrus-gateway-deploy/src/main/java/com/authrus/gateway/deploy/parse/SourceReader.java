package com.authrus.gateway.deploy.parse;

import com.authrus.common.ClassPathReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.PropertyPlaceholderHelper;

@Slf4j
@AllArgsConstructor
public class SourceReader {

   private final PropertyPlaceholderHelper helper;
   private final PropertyResolver resolver;

   public SourceReader(PropertyResolver resolver) {
      this.helper = new PropertyPlaceholderHelper("${", "}");
      this.resolver = resolver;
   }
   
   public Reader readDeployment(String plan) throws IOException {
      String text = readFile(plan);
      
      if(text == null) {
         throw new IllegalStateException("Could not load " + plan);
      }
      String result = helper.replacePlaceholders(text, (name) -> {
         return resolver.getProperty(name);
      });
      return new StringReader(result);
   }
   
   private String readFile(String path) throws IOException {
      String resource = readClassPath(path);
      
      if(resource == null) {
         return readFileSystem(path);
      }
      return null;
   }
   
   private String readFileSystem(String path) throws IOException { 
      try {
         File file = new File(path);
         FileReader reader = new FileReader(file);
         
         return IOUtils.toString(reader);
      } catch(Exception e) {
         log.info("Could not load {} from file system", path, e); 
      }
      return null;
   }
   
   private String readClassPath(String path) throws IOException {
      try {   
         InputStream stream = ClassPathReader.getInputStream(path);
         
         if(stream != null) {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);         
         }
      } catch(Exception e) {
         log.info("Could not load {} from classpath", path, e);         
      }
      return null;
   }
}
