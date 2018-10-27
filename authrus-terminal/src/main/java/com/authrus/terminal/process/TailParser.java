package com.authrus.terminal.process;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TailParser {

   private final Pattern pattern;

   public TailParser() {
      this(".*==>\\s(.*\\.\\w+)\\s<==.*");
   }

   public TailParser(String pattern) {
      this.pattern = Pattern.compile(pattern);
   }

   public Map<String, String> parse(String output) {
      String[] lines = output.split("\\r?\\n");
      String name = "tail";

      if (lines.length > 0) {
         Map<String, String> files = new LinkedHashMap<String, String>();
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < lines.length; i++) {
            Matcher matcher = pattern.matcher(lines[i]);

            if (matcher.matches()) {
               String text = builder.toString();

               files.put(name, text);
               builder.setLength(0);
               name = matcher.group(1);
            } else {
               builder.append(lines[i]);
               builder.append("\r\n");
            }
         }
         String text = builder.toString();

         if (!text.isEmpty()) {
            files.put(name, text);
         }
         return files;
      }
      return Collections.emptyMap();
   }
}
