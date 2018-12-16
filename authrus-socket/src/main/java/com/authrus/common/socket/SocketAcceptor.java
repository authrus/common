package com.authrus.common.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAcceptor implements Acceptor {

   private final ServerSocket serverSocket;
   private final int timeout;

   public SocketAcceptor(int port) throws IOException {
      this(port, 50);
   }

   public SocketAcceptor(int port, int queue) throws IOException {
      this(port, queue, 0);
   }

   public SocketAcceptor(int port, int queue, int timeout) throws IOException {
      this.serverSocket = new ServerSocket(port, queue);
      this.timeout = timeout;
   }

   public ServerSocket getSocket() {
      return serverSocket;
   }

   @Override
   public boolean isConnected() {
      return !serverSocket.isClosed();
   }

   @Override
   public Connection accept() throws IOException {
      Socket socket = serverSocket.accept();

      socket.setTcpNoDelay(true);
      socket.setSoTimeout(timeout);

      return new SocketConnection(socket);
   }

   @Override
   public boolean close() {
      try {
         serverSocket.close();
      } catch (Exception e) {
         return !isConnected();
      }
      return true;
   }
}
