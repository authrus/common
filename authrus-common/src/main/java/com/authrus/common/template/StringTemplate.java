package com.authrus.common.template;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class StringTemplate implements Template {

   private TokenIterator iterator;
   private List<Token> tokens;
   
   public StringTemplate(String template) {
      this.iterator = new TokenIterator(template);
      this.tokens = new ArrayList<Token>();
   }

   @Override
   public void render(TemplateFilter filter, Writer writer) throws Exception {
      while(iterator.hasNext()) {
         Token token = iterator.next();
         
         if(token != null) {
            tokens.add(token);  
         }
      }
      for(Token token : tokens) {
         token.process(filter, writer);
      }
   }
   
   private class TokenIterator {
      
      private char[] source;
      private int off;
      
      public TokenIterator(String template) {
         this.source = template.toCharArray();
      }
      
      public Token next() {
         int mark = off;
         
         while(off < source.length){
            char next = source[off];

            if(next == '$') {
               if(off > mark) {
                  return new TextToken(source, mark, off - mark);
               }
            } else if(off > 0) {
               char prev = source[off - 1];
               
               if(next == '{' && prev == '$') {
                  while(off < source.length) {
                     if(source[off++] == '}') {
                        return new VariableToken(source, mark, off - mark);
                     }
                  }
               }
            }
            off++;
         }
         if(off > mark) {
            return new TextToken(source, mark, off - mark);
         }
         return null;
      } 
      
      public boolean hasNext() {
         return off < source.length;
      }
   }
   
   private interface Token {
      void process(TemplateFilter processor, Writer writer) throws Exception; 
   }
   
   private class TextToken implements Token {
      
      private char[] source;
      private int off;
      private int length;
      
      public TextToken(char[] source, int off, int length) {
         this.source = source;
         this.length = length;
         this.off = off;         
      }
      
      @Override
      public void process(TemplateFilter processor, Writer writer) throws Exception {
         writer.write(source, off, length);
      } 
      
      @Override
      public String toString() {
         return new String(source, off, length);
      }
   }
      
   private class VariableToken implements Token {
      
      private String variable;
      private char[] source;
      private int off;
      private int length;
      
      public VariableToken(char[] source, int off, int length) {
         this.variable = new String(source, off + 2, length - 3);
         this.source = source;
         this.length = length;
         this.off = off;         
      }
      
      @Override
      public void process(TemplateFilter processor, Writer writer) throws Exception {
         Object value = processor.process(variable);
         
         if(value == null) {
            writer.write(source, off, length);
         } else {
            String text = String.valueOf(value);
            writer.append(text);            
         }
      }   
      
      @Override
      public String toString() {
         return new String(source, off, length);
      }
   }
}

