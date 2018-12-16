package com.authrus.common.manage.jmx.socket;

import static javax.management.remote.generic.GenericConnector.MESSAGE_CONNECTION;
import static javax.management.remote.generic.GenericConnectorServer.OBJECT_WRAPPING;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.generic.GenericConnector;
import javax.management.remote.generic.MessageConnection;

import com.sun.jmx.remote.socket.SocketConnection;

/**
 * This provides an implementation of the JMXMP protocol using simple 
 * TCP connections. This can be extended to add additional support for 
 * TLS or to configure the TCP socket connection.
 * 
 * @author Niall Gallagher
 */
public class SocketClientProvider implements JMXConnectorProvider {

   public static final String PROTOCOL = "p2p";

   protected final int timeout;

   public SocketClientProvider() {
      this(60000);
   }

   public SocketClientProvider(int timeout) {
      this.timeout = timeout;
   }

   @Override
   public JMXConnector newJMXConnector(JMXServiceURL address, Map environment) throws IOException {
      String protocol = address.getProtocol();

      if (!protocol.equals(PROTOCOL)) {
         throw new MalformedURLException("Protocol not supported " + protocol);
      }
      return newConnector(address, environment);
   }

   protected JMXConnector newConnector(JMXServiceURL address, Map environment) throws IOException {
      MessageConnection connection = newConnection(address);

      environment.put(MESSAGE_CONNECTION, connection);
      environment.put(OBJECT_WRAPPING, null);

      return new GenericConnector(environment);
   }

   protected MessageConnection newConnection(JMXServiceURL address) throws IOException {
      Socket socket = newSocket(address);

      socket.setTcpNoDelay(true);
      socket.setSoTimeout(timeout);

      return new SocketConnection(socket);
   }

   private Socket newSocket(JMXServiceURL address) throws IOException {
      String host = address.getHost();
      int port = address.getPort();

      return new Socket(host, port);
   }

}
