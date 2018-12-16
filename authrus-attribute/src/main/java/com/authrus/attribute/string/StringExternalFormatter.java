package com.authrus.attribute.string;

public class StringExternalFormatter {    

   public String toExternal(String text) {
      if(text.indexOf('\r') != -1) {
         text = text.replaceAll("\r", "\\\\r");
      }
      if(text.indexOf('\n') != -1) {
         text = text.replaceAll("\n", "\\\\n");
      }
      return text;
   }
   
   public String fromExternal(String text) {
      if(text.indexOf('\\') != -1) {
         text = text.replaceAll("\\\\r", "\r");
      }
      if(text.indexOf('\\') != -1) {
         text = text.replaceAll("\\\\n", "\n");
      }
      return text;
   }
}
