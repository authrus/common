package com.authrus.transport.tunnel;

import org.simpleframework.transport.Channel;

public class Tunnel {

   private final TunnelState state;
   private final Channel channel;
   private final Exception cause;

   public Tunnel(TunnelState state, Channel channel) {
      this(state, channel, null);
   }
   
   public Tunnel(TunnelState state, Channel channel, Exception cause) {
      this.channel = channel;
      this.cause = cause;
      this.state = state;
   }   
   
   public TunnelState getState() {
      return state;
   }
   
   public Exception getCause() {
      return cause;
   }
   
   public Channel getChannel() {
      return channel;
   }
}
