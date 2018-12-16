package com.authrus.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LineSplitter {
   
   public static List<String> splitByLineEnd(Reader text) throws IOException {
      StringBuilder builder = new StringBuilder();
      char[] chunk = new char[8192];
      int count = 0;

      while ((count = text.read(chunk)) != -1) {
         builder.append(chunk, 0, count);
      }
      text.close();
      return splitByLineEnd(builder.toString());
   }

   public static List<String> splitByLineEnd(InputStream text) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] chunk = new byte[8192];
      int count = 0;

      while ((count = text.read(chunk)) != -1) {
         out.write(chunk, 0, count);
      }
      text.close();
      return splitByLineEnd(out.toString("UTF-8"));
   }

   public static List<String> splitByLineEnd(String text) {
      List<Token> list = new LinkedList<Token>();
      char[] array = text.toCharArray();
      int start = 0;

      while(start < array.length) {
         Token line = nextLine(array, start);
         list.add(line);
         start += line.length;
      }
      List<String> lines = new ArrayList<String>(list.size() + 1);
      for(Token token : list) {
         lines.add(token.toString());
      }
      return lines;
   }

   private static Token nextLine(char[] text, int startFrom) {
      for(int i = startFrom; i < text.length; i++) {
         if(text[i] == '\r') {
            if(i + 1 < text.length && text[i + 1] == '\n') {
               i++;
            }
            return new Token(text, startFrom, ++i - startFrom);
         }
         if(text[i] == '\n') {
            return new Token(text, startFrom, ++i - startFrom);
         }
      }
      return new Token(text, startFrom, text.length - startFrom);
   }

   private static class Token {
      private final char[] source;
      private final int start;
      private final int length;
      public Token(char[] source, int start, int length) {
         this.source = source;
         this.start = start;
         this.length = length;
      }
      @Override
      public String toString() {
         for(int i = length - 1; i >= 0; i--) {
            if(!Character.isWhitespace(source[start + i])) {
               return new String(source, start, i + 1);
            }
         }
         return "";
      }
   }
}
