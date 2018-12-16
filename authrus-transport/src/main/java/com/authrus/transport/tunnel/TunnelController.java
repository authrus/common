package com.authrus.transport.tunnel;

import static java.nio.channels.SelectionKey.OP_WRITE;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.reactor.Reactor;

class TunnelController implements TransportProcessor {

   private final TunnelListener listener;
   private final AtomicBoolean active;
   private final Reactor reactor;
   private final String host;
   private final String path;

   public TunnelController(TunnelListener listener, Reactor reactor, String host, String path) throws IOException {
      this.active = new AtomicBoolean(true);   
      this.listener = listener;
      this.reactor = reactor;
      this.host = host;
      this.path = path;
   }   

   @Override
   public void process(Transport transport) throws IOException {
      Channel channel = new TransportChannel(transport);
      ConnectRequestForwarder forwarder = new ConnectRequestForwarder(listener, channel, reactor, host, path);

      if(active.get()) {
         reactor.process(forwarder, OP_WRITE);
      } else {
         listener.onReject(channel);
      }
   }

   @Override
   public void stop() throws IOException {
      active.set(false);
   }
}
