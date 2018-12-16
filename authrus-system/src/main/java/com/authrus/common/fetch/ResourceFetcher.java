package com.authrus.common.fetch;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourceFetcher {

   private final ResourceConverter converter;
   private final ParameterEncoder encoder;
   private final String address;
   private final String method;   
   
   public ResourceFetcher(String method, String address) {
      this.converter = new ResourceConverter();
      this.encoder = new ParameterEncoder();
      this.address = address;
      this.method = method;
   }
   
   public void append(String name, int value) {
      encoder.append(name, value);
   }
   
   public void append(String name, long value) {
      encoder.append(name, value);
   }
   
   public void append(String name, boolean value) {
      encoder.append(name, value);
   }
   
   public void append(String name, double value) {
      encoder.append(name, value);
   }
   
   public void append(String name, String value) {
      encoder.append(name, value);
   }   
   
   public <T> T fetch(Class<T> type) throws Exception {
      if(method.equalsIgnoreCase("POST")) {
         String query = encoder.encode();
         ResourceBody body = fetch(address, query);
         
         return converter.convert(body, type);
      }
      if(method.equalsIgnoreCase("GET")) {
         String query = encoder.encode();
         ResourceBody body = fetch(address + "?" + query);
         
         return converter.convert(body, type);
      }
      throw new IllegalStateException("Method '" + method + "' not supported");
   }   
   
   private ResourceBody fetch(String address) throws Exception {  
      URL target = new URL(address);

      HttpURLConnection connection = (HttpURLConnection) target.openConnection();
      connection.setReadTimeout(10000);
      connection.setConnectTimeout(15000);
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.setDoOutput(false);  

      InputStream source = connection.getInputStream();
      int status = connection.getResponseCode();
      
      return new ResourceBody(source, status);
   }    
   
   private ResourceBody fetch(String address, String body) throws Exception {  
      URL target = new URL(address);

      HttpURLConnection connection = (HttpURLConnection) target.openConnection();
      connection.setReadTimeout(10000);
      connection.setConnectTimeout(15000);
      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      
      byte[] payload = body.getBytes("UTF-8");
      OutputStream output = connection.getOutputStream();

      output.write(payload);
      output.flush();
      output.close();     

      InputStream source = connection.getInputStream();
      int status = connection.getResponseCode();
      
      return new ResourceBody(source, status);
   }  
}
