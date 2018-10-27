package com.authrus.common;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Slf4j
public class PatternMatchingScanner implements ClassPathScanner {

   private final PathMatchingResourcePatternResolver resolver;
   private final String[] packages;
   private final String suffix;
   
   public PatternMatchingScanner(String suffix, String packages) {
      this.resolver = new PathMatchingResourcePatternResolver();
      this.packages = packages.split("\\s*,\\s*");
      this.suffix = suffix;
   }
   
   @Override
   public Set<Class<?>> scan() {
      Set<Class<?>> result = new HashSet<Class<?>>();
      
      for(String location : packages) {
         try {
            ClassPattern pattern = compile(location);
            String match = pattern.getPattern();
            Resource[] resources = resolver.getResources(match);
            
            if(resources.length > 0) {
               Set<Class<?>> matches = filter(location, resources);
               result.addAll(matches);
            }
         } catch(Exception e) {
            log.info("Could not find classes in " + location, e);
         }
      }
      return result;
   }
   
   private Set<Class<?>> filter(String location, Resource[] resources) {
      Set<Class<?>> matches = new HashSet<Class<?>>();
      
      for(Resource resource : resources) {
         try {
            URL target = resource.getURL();
            String path = target.toString();
            String name = resolve(path, location);
            
            if(name.startsWith(location)) {
               ClassLoader loader = ClassPathScanner.class.getClassLoader();
               Class<?> type = loader.loadClass(name);
               
               matches.add(type);
            }
         } catch(Exception e) {
            log.info("Could not load class", e);
         }
      }
      return matches;
   }
   
   private String resolve(String path, String location) {
      int separator = path.lastIndexOf("!");
      
      if(separator != -1) {
         path = path.substring(separator + 1);
      }
      String normal = path.replace('/', '.');
      int index = normal.lastIndexOf(location);
      int length = normal.length();
      
      return normal.substring(index, length - 6);
   }
   
   private ClassPattern compile(String location) {
      String ending = ".class";
      
      if(!location.endsWith(".")) {
         location = location + ".";
      }
      String path = location.replace('.', '/');
      
      if(suffix != null) {
         return new ClassPattern(suffix + ending, location, path);
      }
      return new ClassPattern(ending, location, path);
   }
   
   @Data
   @AllArgsConstructor
   private static class ClassPattern {
      
      private final String suffix;
      private final String location;
      private final String path;
      
      public String getPattern() {
         return CLASSPATH_ALL_URL_PREFIX + path + "**/*" + suffix;
      }
   }
}
