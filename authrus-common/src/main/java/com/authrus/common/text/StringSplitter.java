package com.authrus.common.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class StringSplitter {

   private final String delimiters;

   public StringSplitter() {
      this(",\r\n\t ");
   }
   
   public StringSplitter(String delimiters) {
      this.delimiters = delimiters;
   }

   public List<String> split(String text) {
      if(text != null) {
         StringTokenizer tokenizer = new StringTokenizer(text, delimiters);
         List<String> list = new ArrayList<String>();
         
         while(tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            
            if(!token.isEmpty()) {
               list.add(token);
            }
         }
         return list;
      }
      return Collections.emptyList();
   }
}
