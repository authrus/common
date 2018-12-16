package com.authrus.transport;

import static com.authrus.transport.SocketEvent.CONNECT;
import static com.authrus.transport.SocketEvent.CONNECTION_WAIT;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

import com.authrus.common.ssl.Certificate;

public class SecureTransportBuilder implements TransportBuilder {

   private final BlockingQueue<Transport> queue;
   private final TransportProcessor scheduler;
   private final SocketProcessor adapter;
   private final SocketBuilder builder;
   private final SSLContext context;
   private final Reactor reactor;
   
   public SecureTransportBuilder(SocketBuilder builder, Certificate certificate, Reactor reactor) throws Exception {
      this.queue = new LinkedBlockingQueue<Transport>();
      this.scheduler = new SecureTransportScheduler(queue);
      this.adapter = new SecureSocketProcessor(scheduler);      
      this.context = certificate.getContext();
      this.builder = builder;
      this.reactor = reactor;
   }   

   public Transport connect() throws IOException {      
      try {   
         SSLEngine engine = context.createSSLEngine();
         Socket socket = builder.connect(engine);
         SocketChannel channel = socket.getChannel();
         Trace trace = socket.getTrace();
         
         trace.trace(CONNECT);
         
         if(!channel.finishConnect()) {
            Operation operation = new SecureConnectionFinisher(adapter, socket, reactor);            
            
            trace.trace(CONNECTION_WAIT);
            reactor.process(operation, OP_CONNECT);
         } else {
            adapter.process(socket);
         }
         return queue.poll(1, MINUTES);
      } catch(Exception e) {
         throw new IOException("Error waiting for handshake", e);
      } 
   }
   
   @Override
   public String toString() {
      return builder.toString();
   }
}
