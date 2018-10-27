package com.authrus.rest.container;

import java.net.InetAddress;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerName {

   private final String name;
   
   public String getName(int port) {
      try {
         InetAddress address = InetAddress.getLocalHost();
         String host = address.getCanonicalHostName();
         
         return String.format("%s@%s:%s", name, host, port);
      } catch(Exception e) {
         return name;
      }
   }
}
