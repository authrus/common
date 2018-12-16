package com.authrus.common.util;

public class StringSplitter {
   
   private static final char[] SPACES = {'\r', '\n', '\t', ' '};

   public static String[] splitBySpaces(String text) {
      int numberOfChars = text.length();

      if(numberOfChars > 0) {
         boolean ignore = true;
         
         for(int i = 0; i < SPACES.length; i++) {
            char space = SPACES[i];
            
            if(text.indexOf(space) != -1) {
               ignore = false;
            }
         }
         if(!ignore) {
            int[] tokenLength = new int[numberOfChars * 2];
            int mark = 0;
            int count = 0;
   
            for(int i = 0; i < numberOfChars; i++) {
               char ch = text.charAt(i);
               if(ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                  tokenLength[count++] = mark;
                  tokenLength[count++] = i;
                  
                  while(ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
                     mark = ++i;
   
                     if(i >= numberOfChars) {
                        break;
                     }
                     ch = text.charAt(i);
                  }
               }
            }
            if(mark < numberOfChars) {
               tokenLength[count++] = mark;
               tokenLength[count++] = numberOfChars;
            }
            String[] list = new String[count / 2];
            
            for(int i = 0; i < count ; i+=2) {
               list[i/2] = text.substring(tokenLength[i], tokenLength[i + 1]);
            }
            return list;
         }
         return new String[]{text};
      }
      return new String[0];
   }
}
