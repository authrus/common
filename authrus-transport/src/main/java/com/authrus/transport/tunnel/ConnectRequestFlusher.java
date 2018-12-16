package com.authrus.transport.tunnel;

import static com.authrus.transport.tunnel.TunnelEvent.FORWARD_DONE;
import static com.authrus.transport.tunnel.TunnelEvent.FORWARD_START;

import java.io.IOException;

import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

class ConnectRequestFlusher {
   
   private final ConnectRequestBuilder builder;
   private final ByteWriter writer;
   private final Trace trace;
   
   public ConnectRequestFlusher(Channel channel, Reactor reactor, String host, String path) throws IOException {
      this.builder = new ConnectRequestBuilder(host, path);
      this.writer = channel.getWriter();
      this.trace = channel.getTrace();     
   } 
   
   public void flush() throws IOException {
      String header = builder.createRequest();
      
      try {
         byte[] buffer = header.getBytes("ISO-8859-1"); 
         int count = buffer.length;
         
         if(count > 0) {
            trace.trace(FORWARD_START, count);
         }        
         writer.write(buffer);
         writer.flush();
      } finally {
         trace.trace(FORWARD_DONE, header);
      }         
   }   
}
