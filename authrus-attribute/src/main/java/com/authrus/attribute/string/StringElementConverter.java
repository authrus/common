package com.authrus.attribute.string;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StringElementConverter {

   private final StringExternalFormatter formatter;
   private final Map<String, String> values;
   
   public StringElementConverter(Map<String, String> values) {
      this.formatter = new StringExternalFormatter();
      this.values = values;
   }

   public void write(StringWriter writer) throws IOException {
      Map<String, String> message = new TreeMap<String, String>(values);
      
      if(!message.isEmpty()) {
         Set<String> keys = values.keySet();
   
         for(String key : keys) {
            String attribute = values.get(key);
            String name = formatter.toExternal(key);
            String value = formatter.toExternal(attribute);
            
            writer.append(name);
            writer.append("=");
            writer.append(value);
            writer.append("\r\n");
         }        
      }
   }

   public void read(StringReader reader) throws IOException {
      LineNumberReader lines = new LineNumberReader(reader);
      
      while(lines.ready()) {
         String line = lines.readLine();
         
         if(line == null) {
            break;
         }
         int split = line.indexOf('=');
         int length = line.length();
         
         if(split != -1) {
            String name = line.substring(0, split);
            String value = line.substring(split + 1, length);
            String key = formatter.fromExternal(name);
            String attribute = formatter.fromExternal(value);

            values.put(key, attribute);
         }
      }
   }
}















