package com.authrus.rest.swagger;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

import java.util.Arrays;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Slf4j
public class SwaggerPathLogger {
   
   private static String SWAGGER_PATTERN = "**/swagger-ui.js";
   
   private final PathMatchingResourcePatternResolver resolver;
   
   public SwaggerPathLogger() {
      this.resolver = new PathMatchingResourcePatternResolver();
   }
   
   @SneakyThrows
   public void log() {
      Resource[] matches = resolver.getResources(CLASSPATH_ALL_URL_PREFIX + SWAGGER_PATTERN);
      Arrays.asList(matches)
            .stream()
            .map(resource -> {
               String path = String.valueOf(resource);
               int index = path.lastIndexOf("!");
               
               if(index != -1) {
                  return path.substring(index + 1);
               }
               return path;
            })
            .forEach(path -> {   
               log.info("Swagger {}", path);
            });
   }
}
