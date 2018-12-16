package com.authrus.common.manage.jmx.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.generic.MessageConnection;
import javax.management.remote.generic.MessageConnectionServer;

import com.sun.jmx.remote.socket.SocketConnection;

public class SocketConnectionServer implements MessageConnectionServer {

   private JMXServiceURL address;
   private ServerSocket server;
   private int timeout;

   public SocketConnectionServer(JMXServiceURL address) {
      this(address, 60000);
   }

   public SocketConnectionServer(JMXServiceURL address, int timeout) {
      this.address = address;
      this.timeout = timeout;
   }

   @Override
   public MessageConnection accept() throws IOException {
      Socket socket = server.accept();

      socket.setSoTimeout(timeout);
      socket.setTcpNoDelay(true);

      return new SocketConnection(socket);
   }

   @Override
   public void start(Map environment) throws IOException {
      int port = address.getPort();

      if (server == null) {
         server = new ServerSocket(port);
      }
   }

   @Override
   public void stop() throws IOException {
      if (server != null) {
         server.close();
      }
      server = null;
   }

   @Override
   public JMXServiceURL getAddress() {
      return address;
   }

   @Override
   public String toString() {
      return address.toString();
   }
}
