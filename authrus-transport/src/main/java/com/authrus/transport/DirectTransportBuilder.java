package com.authrus.transport;

import static com.authrus.transport.SocketEvent.CONNECT;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketTransport;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

public class DirectTransportBuilder implements TransportBuilder {

   private final SocketBuilder builder;
   private final Reactor reactor;
   
   public DirectTransportBuilder(SocketBuilder builder, Reactor reactor) {
      this.builder = builder;
      this.reactor = reactor;
   }

   @Override
   public Transport connect() throws IOException {      
      Socket socket = builder.connect();
      SocketChannel channel = socket.getChannel();
      Trace trace = socket.getTrace();
      
      trace.trace(CONNECT);
      channel.finishConnect();

      return new SocketTransport(socket, reactor);
   }
   
   @Override
   public String toString() {
      return builder.toString();
   }
}
