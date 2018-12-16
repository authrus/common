package com.authrus.common.socket.proxy.analyser;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class ConnectionContext {

   private final Connection destination;
   private final Connection source;

   public ConnectionContext(Connection source, Connection destination) {
      this.destination = destination;
      this.source = source;
   }

   public HostAddress getSourceLocalAddress() {
      return source.getLocalAddress();
   }

   public HostAddress getSourceRemoteAddress() {
      return source.getRemoteAddress();
   }

   public HostAddress getDestinationLocalAddress() {
      return destination.getLocalAddress();
   }

   public HostAddress getDestinationRemoteAddress() {
      return destination.getRemoteAddress();
   }
}
