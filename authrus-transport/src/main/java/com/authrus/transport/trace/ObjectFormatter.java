package com.authrus.transport.trace;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ObjectFormatter {
   
   public String format(Object value) { 
      ValueFormatter formatter = create(value);
      
      if(formatter != null) {
         return formatter.format(value);
      }
      return null;
   }
   
   private ValueFormatter create(Object value) {
      if (value == null) {
         return new NullFormatter();
      }
      if (value instanceof Throwable) {
         return new ThrowableFormatter();
      }
      return new ValueFormatter();
   }
   
   private class ValueFormatter<T> {      
      
      public String format(T value) {
         return String.valueOf(value);
      }
   }

   private class ThrowableFormatter extends ValueFormatter<Throwable> {

      @Override
      public String format(Throwable cause) {
         StringWriter buffer = new StringWriter();
         PrintWriter writer = new PrintWriter(buffer);
         
         cause.printStackTrace(writer);
         writer.close();
         
         return buffer.toString();
      }
   }
   
   private class NullFormatter extends ValueFormatter<Object> {

      @Override
      public String format(Object value) {
         return "";
      }
   }  
}
