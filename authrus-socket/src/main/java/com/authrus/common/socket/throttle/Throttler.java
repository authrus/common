package com.authrus.common.socket.throttle;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class Throttler {

   private final ThrottleRegistry readRegistry;
   private final ThrottleRegistry writeRegistry;
   private final ThrottleAlarm throttleAlarm;

   public Throttler(ThrottleRegistry readRegistry, ThrottleRegistry writeRegistry) {
      this(readRegistry, writeRegistry, null);
   }

   public Throttler(ThrottleRegistry readRegistry, ThrottleRegistry writeRegistry, ThrottleAlarm throttleAlarm) {
      this.throttleAlarm = throttleAlarm;
      this.readRegistry = readRegistry;
      this.writeRegistry = writeRegistry;
   }

   public Connection throttle(Connection connection) {
      HostAddress remoteAddress = connection.getRemoteAddress();
      ThrottleCapacity readCapacity = readRegistry.resolveCapacity(remoteAddress);
      ThrottleCapacity writeCapacity = writeRegistry.resolveCapacity(remoteAddress);

      return throttle(connection, readCapacity, writeCapacity);
   }

   public Connection throttle(Connection connection, ThrottleCapacity readCapacity, ThrottleCapacity writeCapacity) {
      ThrottleMonitor throttleMonitor = new ThrottleMonitor(readCapacity, writeCapacity);
      Connection throttleConnection = new ThrottleConnection(throttleMonitor, throttleAlarm, connection);

      return throttleConnection;
   }
}
