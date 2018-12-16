package com.authrus.transport.tunnel;

import static com.authrus.transport.SocketEvent.CONNECT;
import static com.authrus.transport.SocketEvent.CONNECTION_WAIT;
import static com.authrus.transport.tunnel.TunnelState.ESTABLISHED;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLEngine;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.SocketWrapper;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

import com.authrus.transport.SocketBuilder;

public class TunnelBuilder implements SocketBuilder {

   private final BlockingQueue<Tunnel> queue;
   private final TransportProcessor processor;
   private final TunnelListener scheduler;
   private final SocketProcessor adapter;
   private final SocketBuilder builder;
   private final Reactor reactor;
   private final String host;
   
   public TunnelBuilder(SocketBuilder builder, Reactor reactor, String host, String path) throws Exception {
      this.queue = new LinkedBlockingQueue<Tunnel>();
      this.scheduler = new TunnelStatusUpdater(queue);
      this.processor = new TunnelController(scheduler, reactor, host, path);
      this.adapter = new TunnelSocketProcessor(processor);   
      this.builder = builder;
      this.reactor = reactor;
      this.host = host;
   }  
   
   @Override
   public Socket connect() throws IOException {
      return connect(null);
   }

   @Override
   public Socket connect(SSLEngine engine) throws IOException { 
      Tunnel tunnel = tunnel(engine);
      
      if(tunnel == null) {
         throw new IOException("Time out waiting for tunnel to connect");
      }
      TunnelState state = tunnel.getState();
      Exception cause = tunnel.getCause();
      
      if(cause != null) {
         throw new IOException("Error occured establishing tunnel", cause);
      }
      if(state != ESTABLISHED) {
         throw new IOException("Result of tunnel connect is " + state);
      }
      Channel channel = tunnel.getChannel();
      SocketChannel socket = channel.getSocket();
      Trace trace = channel.getTrace();
      
      return new SocketWrapper(socket, trace);
   }
  
   protected Tunnel tunnel(SSLEngine engine) throws IOException {
      try {   
         Socket socket = builder.connect(engine);
         SocketChannel channel = socket.getChannel();
         Trace trace = socket.getTrace();
         
         trace.trace(CONNECT);
         
         if(!channel.finishConnect()) {
            Operation operation = new TunnelConnectionFinisher(scheduler, adapter, socket, reactor);            
            
            trace.trace(CONNECTION_WAIT);
            reactor.process(operation, OP_CONNECT);
         } else {
            adapter.process(socket);
         }
         return queue.poll(1, MINUTES);
      } catch(Exception e) {
         throw new IOException("Error waiting for tunnel", e);
      } 
   }

   @Override
   public String toString() {
      return String.format("%s@%s", host, builder);
   }
}
