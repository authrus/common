package com.authrus.transport.tunnel;

import static com.authrus.transport.tunnel.TunnelEvent.CANCEL;
import static com.authrus.transport.tunnel.TunnelEvent.ERROR;
import static com.authrus.transport.tunnel.TunnelEvent.READ_WAIT;
import static com.authrus.transport.tunnel.TunnelEvent.REMOTE_CONNECTION_CLOSE;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

class ConnectRequestForwarder implements Operation {

   private final ConnectResponseCollector collector;
   private final ConnectRequestFlusher flusher;
   private final TunnelListener listener;
   private final ByteCursor cursor;
   private final Channel channel;
   private final Trace trace;
   
   public ConnectRequestForwarder(TunnelListener listener, Channel channel, Reactor reactor, String host, String path) throws IOException {
      this.collector = new ConnectResponseCollector(listener, channel, reactor);
      this.flusher = new ConnectRequestFlusher(channel, reactor, host, path);
      this.cursor = channel.getCursor();
      this.trace = channel.getTrace();
      this.listener = listener;
      this.channel = channel;
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
   public void run() {
      try {
         if(!cursor.isOpen()) {
            trace.trace(REMOTE_CONNECTION_CLOSE);
            listener.onReject(channel);
            channel.close();
         } else {
            flusher.flush();
            trace.trace(READ_WAIT);
            collector.run();
         }
      } catch (Exception cause) {
         trace.trace(ERROR, cause);
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
