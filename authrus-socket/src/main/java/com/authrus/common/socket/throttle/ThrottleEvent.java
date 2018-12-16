package com.authrus.common.socket.throttle;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class ThrottleEvent {

   private final ThrottleResult throttleResult;
   private final Connection connection;
   private final long packetSize;
   private final long totalSize;

   public ThrottleEvent(ThrottleResult throttleResult, Connection connection, long totalSize, long packetSize) {
      this.throttleResult = throttleResult;
      this.packetSize = packetSize;
      this.totalSize = totalSize;
      this.connection = connection;
   }

   public ThrottleResult getThrottleResult() {
      return throttleResult;
   }

   public HostAddress getLocalAddress() {
      return connection.getLocalAddress();
   }

   public HostAddress getRemoteAddress() {
      return connection.getRemoteAddress();
   }

   public long getTotalSize() {
      return totalSize;
   }

   public long getPacketSize() {
      return packetSize;
   }
}
