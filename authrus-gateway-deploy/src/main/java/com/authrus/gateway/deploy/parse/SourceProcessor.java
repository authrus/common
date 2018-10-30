package com.authrus.gateway.deploy.parse;

import java.io.LineNumberReader;
import java.io.Reader;

import lombok.SneakyThrows;

public class SourceProcessor {

   @SneakyThrows
   public static String process(Reader reader) {
      LineNumberReader iterator = new LineNumberReader(reader);
      StringBuilder builder = new StringBuilder();
      String line = null;
      
      while((line = iterator.readLine()) != null) {
         if(!line.trim().matches("^\\s*//.*") && !line.trim().isEmpty()) {
            builder.append(line);
            builder.append("\n");
         }
      }
      reader.close();
      return builder.toString();      
   }
}
