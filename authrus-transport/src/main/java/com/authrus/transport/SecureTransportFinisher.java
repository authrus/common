package com.authrus.transport;

import static com.authrus.transport.SocketEvent.CANCEL;
import static com.authrus.transport.SocketEvent.CONNECTION_WAIT;
import static com.authrus.transport.SocketEvent.ERROR;
import static com.authrus.transport.SocketEvent.WRITE_WAIT;
import static java.nio.channels.SelectionKey.OP_CONNECT;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

class SecureConnectionFinisher implements Operation {

   private final SocketProcessor processor;
   private final SocketChannel channel;
   private final Reactor reactor;
   private final Socket socket;
   private final Trace trace;
   
   public SecureConnectionFinisher(SocketProcessor processor, Socket socket, Reactor reactor) throws IOException {
      this.channel = socket.getChannel();
      this.trace = socket.getTrace();
      this.processor = processor;
      this.reactor = reactor;
      this.socket = socket;
   }
   
   @Override
   public Trace getTrace() {    
      return trace;
   }     
   
   @Override
   public SelectableChannel getChannel() {
      return channel;
   }
   
   @Override
   public void run() {
      try {
         if(channel.finishConnect()) {
            trace.trace(WRITE_WAIT);
            processor.process(socket);
         } else {
            trace.trace(CONNECTION_WAIT);
            reactor.process(this, OP_CONNECT);
         }
      } catch (Exception cause) {
         trace.trace(ERROR, cause);
         
         try {
            channel.close();
         } catch(Exception close) {
            trace.trace(ERROR, close);
         }
      }
   }

   @Override
   public void cancel() {
      try {
         trace.trace(CANCEL);
         channel.close();
      } catch(Exception cause) {
         trace.trace(ERROR, cause);
      }
   }
}
