package com.authrus.common.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

public class InputStreamConsole implements Console {

   private final LineNumberReader parser;
   private final StringBuilder builder;
   private final Reader reader;

   public InputStreamConsole(InputStream source, String command) {
      this.reader = new InputStreamReader(source);
      this.parser = new LineNumberReader(reader);
      this.builder = new StringBuilder(command);
   }

   public String readAll() throws IOException {
      while (true) {
         String line = parser.readLine();

         if (line != null) {
            builder.append("\r\n");
            builder.append(line);
         } else {
            break;
         }
      }
      return builder.toString();
   }

   @Override
   public String readLine() throws IOException {
      return parser.readLine();
   }

}
