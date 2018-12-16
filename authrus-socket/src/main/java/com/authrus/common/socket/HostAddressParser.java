package com.authrus.common.socket;

public class HostAddressParser {

   public HostAddressParser() {
      super();
   }

   public HostAddress[] parse(String soure) {
      String[] tokens = soure.split(",");

      if (tokens.length > 0) {
         HostAddress[] list = new HostAddress[tokens.length];

         for (int i = 0; i < tokens.length; i++) {
            String address = tokens[i].trim();
            String[] pair = address.split(":");

            if (pair.length != 2) {
               throw new IllegalArgumentException("Could not parse address " + address);
            }
            String host = pair[0];
            int port = Integer.parseInt(pair[1]);

            list[i] = new HostAddress(host, port);
         }
         return list;
      }
      return new HostAddress[0];
   }
}
