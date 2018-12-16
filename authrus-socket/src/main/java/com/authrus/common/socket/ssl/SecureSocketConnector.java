package com.authrus.common.socket.ssl;

import java.io.IOException;
import java.net.Socket;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.Connector;
import com.authrus.common.socket.SocketConnection;

public class SecureSocketConnector implements Connector {

   private final SecureSocketBuilder builder;
   private final String host;
   private final int port;
   private final int timeout;

   public SecureSocketConnector(SecureSocketBuilder builder, String host, int port) throws Exception {
      this(builder, host, port, 0);
   }

   public SecureSocketConnector(SecureSocketBuilder builder, String host, int port, int timeout) throws Exception {
      this.builder = builder;
      this.timeout = timeout;
      this.host = host;
      this.port = port;
   }

   @Override
   public Connection connect() throws IOException {
      Socket socket = builder.createSocket(host, port);

      socket.setTcpNoDelay(true);
      socket.setSoTimeout(timeout);

      return new SocketConnection(socket);
   }
}
