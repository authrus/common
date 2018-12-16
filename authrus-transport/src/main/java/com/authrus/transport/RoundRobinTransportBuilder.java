package com.authrus.transport;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.simpleframework.transport.Transport;

public class RoundRobinTransportBuilder implements TransportBuilder {
   
   private final BlockingQueue<TransportBuilder> builders;
   
   public RoundRobinTransportBuilder(Set<TransportBuilder> builders) {
      this.builders = new LinkedBlockingQueue<TransportBuilder>(builders);
   }

   @Override
   public Transport connect() throws IOException {
      try {
         TransportBuilder builder = builders.take();
         
         try {
            return builder.connect();
         } finally {
            builders.offer(builder);
         }
      } catch(Exception e) {
         throw new IOException("Unable to create a transport connection", e);
      }
   }

}
