package com.authrus.transport.tunnel;

import java.io.IOException;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.TransportSocketProcessor;

class TunnelSocketProcessor implements SocketProcessor {
   
   private final SocketProcessor connector;
   
   public TunnelSocketProcessor(TransportProcessor connector) throws IOException {
      this(connector, 8192);
   }
   
   public TunnelSocketProcessor(TransportProcessor connector, int buffer) throws IOException {
      this(connector, buffer, 20480);
   }
   
   public TunnelSocketProcessor(TransportProcessor connector, int buffer, int threshold) throws IOException {
      this.connector = new TransportSocketProcessor(connector, 1, buffer, threshold, true);      
   }

   @Override
   public void process(Socket socket) throws IOException {
      connector.process(socket);
   }

   @Override
   public void stop() throws IOException {
      connector.stop();
   }

}
