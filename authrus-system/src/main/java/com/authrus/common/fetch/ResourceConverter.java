package com.authrus.common.fetch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ResourceConverter {

   public <T> T convert(ResourceBody body, Class<T> type) throws Exception {
      BodyConverter<T> converter = resolve(type);
      
      if(converter != null) {         
         return (T)converter.convert(body);
      }
      throw new IllegalArgumentException("No converter for " + type);
   }
   
   private BodyConverter resolve(Class type) throws Exception {
      if(type.equals(String.class)) {
         return new StringConverter();
      } 
      if(type.equals(InputStream.class)) {
         return new InputStreamConverter();
      } 
      if(type.equals(Properties.class)) {
         return new PropertiesConverter();
      } 
      if(type.equals(ResourceBody.class)) {
         return new ResourceBodyConverter();
      } 
      if(type.equals(byte[].class)) {
         return new ByteArrayConverter();
      } 
      return null;
   }   
   
   private interface BodyConverter<T> {
      T convert(ResourceBody body) throws Exception;
   }
   
   private class ResourceBodyConverter implements BodyConverter<ResourceBody> {

      private final InputStreamConverter converter;     
      
      public ResourceBodyConverter() {
         this.converter = new InputStreamConverter();
      }

      @Override
      public ResourceBody convert(ResourceBody body) throws Exception {
         InputStream source = converter.convert(body);
         int status = body.getStatus();
         
         return new ResourceBody(source, status);
      }      
   }
   
   private class InputStreamConverter implements BodyConverter<InputStream> {

      private final ByteArrayOutputStream buffer;
      private final byte[] chunk;
      
      public InputStreamConverter() {
         this.buffer = new ByteArrayOutputStream();
         this.chunk = new byte[1024];
      }

      @Override
      public InputStream convert(ResourceBody body) throws Exception {
         InputStream source = body.getSource();
         int count = 0;
         
         while((count = source.read(chunk)) != -1) {
            buffer.write(chunk, 0, count);
         }
         byte[] data = buffer.toByteArray();
         
         return new ByteArrayInputStream(data);
      }
      
   }
   
   private class StringConverter implements BodyConverter<String> {
      
      private final ByteArrayOutputStream buffer;
      private final byte[] chunk;
      
      public StringConverter() {
         this.buffer = new ByteArrayOutputStream();
         this.chunk = new byte[1024];
      }

      @Override
      public String convert(ResourceBody body) throws Exception {
         InputStream source = body.getSource();
         int count = 0;
         
         while((count = source.read(chunk)) != -1) {
            buffer.write(chunk, 0, count);
         }
         return buffer.toString("UTF-8");
      }      
   }   
   
   private class ByteArrayConverter implements BodyConverter<byte[]> {
      
      private final ByteArrayOutputStream buffer;
      private final byte[] chunk;
      
      public ByteArrayConverter() {
         this.buffer = new ByteArrayOutputStream();
         this.chunk = new byte[1024];
      }

      @Override
      public byte[] convert(ResourceBody body) throws Exception {
         InputStream source = body.getSource();
         int count = 0;
         
         while((count = source.read(chunk)) != -1) {
            buffer.write(chunk, 0, count);
         }
         return buffer.toByteArray();
      }      
   }   
   
   private class PropertiesConverter implements BodyConverter<Properties> {
      
      private final Properties properties;
      
      public PropertiesConverter() {
         this.properties = new Properties();
      }

      @Override
      public Properties convert(ResourceBody body) throws Exception {
         InputStream source = body.getSource();
         
         properties.clear();
         properties.load(source);
         
         return properties;
      }      
   }       
}
