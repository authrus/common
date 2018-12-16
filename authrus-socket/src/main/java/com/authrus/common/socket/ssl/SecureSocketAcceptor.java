package com.authrus.common.socket.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.authrus.common.socket.Acceptor;
import com.authrus.common.socket.Connection;
import com.authrus.common.socket.SocketConnection;

public class SecureSocketAcceptor implements Acceptor {

   private final ServerSocket acceptor;
   private final int timeout;

   public SecureSocketAcceptor(SecureSocketBuilder builder, int port) throws Exception {
      this(builder, port, 0);
   }

   public SecureSocketAcceptor(SecureSocketBuilder builder, int port, int timeout) throws Exception {
      this.acceptor = builder.createServerSocket(port);
      this.timeout = timeout;
   }

   @Override
   public Connection accept() throws IOException {
      Socket socket = acceptor.accept();

      socket.setTcpNoDelay(true);
      socket.setSoTimeout(timeout);

      return new SocketConnection(socket);
   }

   @Override
   public boolean isConnected() {
      return !acceptor.isClosed();
   }

   @Override
   public boolean close() {
      try {
         acceptor.close();
      } catch (Exception e) {
         return !isConnected();
      }
      return true;
   }
}
