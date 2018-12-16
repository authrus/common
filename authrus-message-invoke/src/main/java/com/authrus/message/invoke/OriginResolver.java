package com.authrus.message.invoke;

import java.io.IOException;
import java.net.InetAddress;

import com.authrus.tuple.query.Origin;

public class OriginResolver {

   private final String source;

   public OriginResolver(String source) {
      this.source = source;
   }

   public Origin resolveOrigin() throws IOException {
      InetAddress address = InetAddress.getLocalHost();
      String host = address.getHostName();

      return new Origin(source, host);
   }
}
