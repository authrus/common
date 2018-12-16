package com.authrus.common.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.authrus.common.parse.Parser;

public class FunctionParser extends Parser {

   private final List<FunctionParameter> parameters;
   private final TokenList navigation;
   private final Token function;
   private final Token name;
   private final Token value;

   public FunctionParser() {
      this.parameters = new ArrayList<FunctionParameter>();
      this.navigation = new TokenList();
      this.function = new Token();
      this.value = new Token();
      this.name = new Token();
   }

   public FunctionParser(String text) {
      this();
      parse(text);
   }

   public String getName() {
      return function.toString();
   }

   public String[] getNavigation() {
      return navigation.list();
   }

   public List<FunctionParameter> getParameters() {
      return Collections.unmodifiableList(parameters);
   }

   @Override
   protected void init() {
      function.clear();
      value.clear();
      name.clear();
      parameters.clear();
      navigation.clear();
   }

   @Override
   protected void parse() {
      pack();
      function();
      parameters();
   }

   private void pack() {
      int pos = 0;

      while(off < count){
         if(quote(source[off])){
            char open = source[off];

            while(off < count) {
               source[pos++] = source[off++];

               if(source[off] == open) {
                  source[pos++] = source[off++];
                  break;
               }
            }
         } else if(!space(source[off])) {
            source[pos++] = source[off++];
         } else {
            off++;
         }
      }
      count = pos;
      off = 0;
   }

   private void function() {
      function.off = 0;

      while(off < count) {
         char next = source[off++];

         if(reference(next)) {
            navigation.add(function.off, function.len);
            function.off = off;
            function.len = - 1;
         }
         if(terminal(next)) {
            break;
         }
         function.len++;
      }
   }

   private void parameters() {
      while(off < count) {
         parameter();
      }
   }

   private void parameter() {
      name();
      value();
      insert();
      reset();
   }

   private void name() {
      name.off = off;

      while(off < count) {
         char next = source[off++];

         if(declaration(next)) {
            break;
         }
         name.len++;
      }
   }

   private void value() {
      value.off = off;

      while(off < count) {
         char next = source[off++];

         if(quote(next)) {
            value.quote = next;

            if(value.len == 0) {
               value.off++;
               continue;
            }
         } else {
            if(terminal(next)) {
               char prev = source[off-2];

               if(prev == value.quote) {
                  value.len--;
               } else {
                  value.quote = 0;
               }
               break;
            }
         }
         value.len++;
      }
   }

   private void reset() {
      name.clear();
      value.clear();
   }

   private void insert() {
      String key = name.toString();
      String argument = value.toString();
      boolean quote = value.isQuote();

      insert(key,  argument, quote);
   }

   private void insert(String name, String value, boolean quote) {
      FunctionParameter parameter = new FunctionParameter(name, value, quote);
      parameters.add(parameter);
   }

   private boolean declaration(char ch) {
      return ch == ':';
   }

   private boolean reference(char ch) {
      return ch == '.';
   }

   private boolean terminal(char ch) {
      return ch == ',' || ch == '(' || ch == ')';
   }

   private class Token {

      public String cache;
      public char quote;
      public int off;
      public int len;

      public boolean isQuote() {
         return quote != 0;
      }

      public void clear() {
         cache = null;
         quote = 0;
         len = 0;
      }

      public String toString() {
         if(cache != null) {
            return cache;
         }
         if(len > 0) {
            cache = new String(source,off,len);
         }
         return cache;
      }
   }

   private class TokenList {

      private String[] cache;
      private int[] list;
      private int count;

      public TokenList(){
         list = new int[16];
      }

      public String segment(int from) {
         int total = count / 2;
         int left = total - from;

         return segment(from, left);
      }

      public String segment(int from, int total) {
         int last = list[0] + list[1] + 1;

         if(from + total < count / 2) {
            last = offset(from + total);
         }
         int start = offset(from);
         int length = last - start;

         return new String(source, start-1, length);
      }

      private int offset(int segment) {
         int last = count - 2;
         int shift = segment * 2;
         int index = last - shift;

         return list[index];
      }

      public void add(int off, int len){
         if(count+1 > list.length) {
            resize(count *2);
         }
         list[count++] = off;
         list[count++] = len;
      }

      public String[] list(){
         if(cache == null) {
            cache = build();
         }
         return cache;
      }

      private String[] build(){
         String[] value = new String[count/2];

         for(int i =0; i< count; i+=2){
            int index = (i/2);
            int off = list[i];
            int size = list[i + 1];

            value[index] = new String(source, off, size);
         }
         return value;
      }

      public void clear(){
         cache =null;
         count =0;
      }

      private void resize(int size){
         int[] copy = new int[size];
         System.arraycopy(list,0,copy,0,count);
         list = copy;
      }
   }

}
