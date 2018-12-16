package com.authrus.transport.tunnel;

import static com.authrus.transport.tunnel.TunnelEvent.CANCEL;
import static com.authrus.transport.tunnel.TunnelEvent.ERROR;
import static com.authrus.transport.tunnel.TunnelEvent.READ_WAIT;
import static com.authrus.transport.tunnel.TunnelEvent.REMOTE_CONNECTION_CLOSE;
import static com.authrus.transport.tunnel.TunnelEvent.RESPONSE_DONE;
import static java.nio.channels.SelectionKey.OP_READ;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

class ConnectResponseCollector implements Operation {

   private final ConnectResponseConsumer consumer;
   private final TunnelListener listener;
   private final ByteCursor cursor;
   private final Reactor reactor;
   private final Channel channel;
   private final Trace trace;
   
   public ConnectResponseCollector(TunnelListener listener, Channel channel, Reactor reactor) throws IOException {
      this.consumer = new ConnectResponseConsumer();
      this.cursor = channel.getCursor();
      this.trace = channel.getTrace();
      this.listener = listener;
      this.channel = channel;
      this.reactor = reactor;
   }  

   @Override
   public Trace getTrace() {    
      return trace;
   }   
   
   @Override
   public SelectableChannel getChannel() {
      return channel.getSocket();
   }
   
   @Override
   public void run()  {
      try {       
         while(cursor.isReady()) {
            if(consumer.isFinished()) {
               break;
            }
            consumer.consume(cursor);
         }
         if(consumer.isFinished()) {
            String header = consumer.getHeader();
            TunnelState state = consumer.getState();
            
            trace.trace(RESPONSE_DONE, header);
            listener.onResponse(channel, state);
         } else {
            if(cursor.isOpen()) {
               trace.trace(READ_WAIT);
               reactor.process(this, OP_READ);
            } else {
               trace.trace(REMOTE_CONNECTION_CLOSE);
               listener.onReject(channel);
               channel.close();
            }
         }
      }catch(Exception cause) {
         trace.trace(ERROR, cause);
         listener.onFailure(channel, cause);
         channel.close();
      }     
   }
   
   @Override
   public void cancel() {
      try {
         trace.trace(CANCEL);
         listener.onReject(channel);
         channel.close();
      } catch(Exception cause) {
         trace.trace(ERROR, cause);
         listener.onFailure(channel, cause);
         channel.close();
      }
   }
}
