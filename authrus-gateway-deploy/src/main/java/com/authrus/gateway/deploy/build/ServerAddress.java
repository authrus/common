package com.authrus.gateway.deploy.build;

import java.net.InetAddress;
import java.net.URI;

import lombok.AllArgsConstructor;

@AllArgsConstructor 
class ServerAddress {
   
   private String address;
   private String path;

   
   public String getPath() {
      return path;
   }
   
   public boolean isSecure() {
      URI target = URI.create(address);
      String scheme = target.getScheme();
      
      return "https".equalsIgnoreCase(scheme);
   }
   
   public int getPort() {
      URI target = URI.create(address);
      String scheme = target.getScheme();
      boolean secure = "https".equalsIgnoreCase(scheme);
      int port = target.getPort();
      
      if(port == -1) {
         return secure ? 443 : 80;
      }
      return port;
   }
   
   public String getIPAddress(){
      String host = getHost();
      try {
         return InetAddress.getByName(host).getHostAddress();
      }catch(Exception e){
         return host;
      }
   }
   
   public String getHost() {
      URI target = URI.create(address);
      return target.getHost();
   }
   
   public String getHostHeader() {
      URI target = URI.create(address);
      String scheme = target.getScheme();
      boolean secure = "https".equalsIgnoreCase(scheme);
      String host = target.getHost();
      int port = target.getPort();
      
      if(port == -1) {
         return host;
      }
      if(secure && port == 443) {
         return host;
      }
      if(!secure && port == 80) {
         return host;
      }
      return host + ":" + port;
   }

   public String getFullAddress() {
      URI target = URI.create(address);
      String scheme = target.getScheme();
      boolean secure = "https".equalsIgnoreCase(scheme);
      String host = target.getHost();
      int port = target.getPort();
      
      if(port == -1) {
         return scheme + "://" + host + (path == null ? "/" : path);
      }
      if(secure && port == 443) {
         return scheme + "://" + host + (path == null ? "/" : path);
      }
      if(!secure && port == 80) {
         return scheme + "://" + host + (path == null ? "/" : path);
      }
      return scheme + "://" + host + ":" + port + (path == null ? "/" : path);
   }
   
   public URI getURI() {
      URI target = URI.create(address);
      String scheme = target.getScheme();
      boolean secure = "https".equalsIgnoreCase(scheme);
      String host = target.getHost();
      int port = target.getPort();
      
      if(port == -1) {
         port = secure ? 443 : 80;
      }
      return URI.create(scheme + "://" + host + ":" + port + (path == null ? "/" : path));
   }
   
   @Override
   public String toString() {
      return getFullAddress();
   }
}