package com.authrus.common.socket.throttle;

import java.io.IOException;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.Connector;

public class ThrottleConnector implements Connector {

   private final Throttler throttler;
   private final Connector connector;

   private ThrottleConnector(Throttler throttler, Connector connector) {
      this.throttler = throttler;
      this.connector = connector;
   }

   @Override
   public Connection connect() throws IOException {
      Connection connection = connector.connect();
      return throttler.throttle(connection);
   }
}
