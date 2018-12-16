package com.authrus.common.socket;

import java.io.IOException;
import java.net.Socket;

public class SocketConnector implements Connector {

   private final String host;
   private final int port;
   private final int timeout;

   public SocketConnector(String host, int port) {
      this(host, port, 0);
   }

   public SocketConnector(String host, int port, int timeout) {
      this.timeout = timeout;
      this.host = host;
      this.port = port;
   }

   @Override
   public Connection connect() throws IOException {
      try {
         Socket socket = new Socket(host, port);

         socket.setTcpNoDelay(true);
         socket.setSoTimeout(timeout);

         return new SocketConnection(socket);
      } catch(Exception e) {
         throw new IOException("Could not connect to " + host + ":" + port, e);
      }
   }
}
