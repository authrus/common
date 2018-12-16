package com.authrus.common.socket;

public class HostAddress {

   private final String host;
   private final int port;

   public HostAddress(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }

   public String toString() {
      return host + ":" + port;
   }

}
