package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameEvent.CANCEL;
import static com.authrus.tuple.frame.FrameEvent.CONNECTION_WAIT;
import static com.authrus.tuple.frame.FrameEvent.ERROR;
import static java.nio.channels.SelectionKey.OP_CONNECT;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

class FrameConnectionFinisher implements Operation {

   private final FrameListener listener;
   private final SocketChannel socket;
   private final Reactor reactor;
   private final Channel channel;
   private final Runnable task;
   private final Trace trace;
   
   public FrameConnectionFinisher(FrameListener listener, Channel channel, Reactor reactor, Runnable task) throws IOException {
      this.socket = channel.getSocket();
      this.trace = channel.getTrace();
      this.listener = listener;
      this.reactor = reactor;
      this.channel = channel;
      this.task = task;
   }
   
   @Override
   public Trace getTrace() {    
      return trace;
   }     
   
   @Override
   public SelectableChannel getChannel() {
      return socket;
   }
   
   @Override
   public void run() {
      try {
         if(socket.finishConnect()) {
            listener.onConnect();
            task.run();
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
