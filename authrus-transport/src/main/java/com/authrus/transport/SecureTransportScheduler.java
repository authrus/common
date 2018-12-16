package com.authrus.transport;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportProcessor;

class SecureTransportScheduler implements TransportProcessor {

   private final BlockingQueue<Transport> queue;
   private final AtomicBoolean active;

   public SecureTransportScheduler(BlockingQueue<Transport> queue) throws IOException {
      this.active = new AtomicBoolean(true);
      this.queue = queue;      
   }   

   @Override
   public void process(Transport transport) throws IOException {
      if(!active.get()) {
         throw new IOException("Connection processor is not active");
      }
      queue.offer(transport);
   }

   @Override
   public void stop() throws IOException {
      active.set(false);
   }
}
