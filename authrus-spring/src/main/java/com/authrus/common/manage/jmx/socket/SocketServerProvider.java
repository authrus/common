package com.authrus.common.manage.jmx.socket;

import static javax.management.remote.generic.GenericConnectorServer.MESSAGE_CONNECTION_SERVER;
import static javax.management.remote.generic.GenericConnectorServer.OBJECT_WRAPPING;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.generic.GenericConnectorServer;
import javax.net.ssl.SSLContext;

/**
 * This provides a server implementation of the JMXMP protocol. This 
 * can be subclasses to provide security via SSL or TLS and also to 
 * configure the low level TCP settings on the socket.
 * 
 * @author Niall Gallagher
 */
public class SocketServerProvider implements JMXConnectorServerProvider {

   public static final String PROTOCOL = "p2p";

   protected final SSLContext context;
   protected final int timeout;

   public SocketServerProvider() throws Exception {
      this(60000);
   }

   public SocketServerProvider(int timeout) throws Exception {
      this.context = SSLContext.getDefault();
      this.timeout = timeout;
   }

   @Override
   public JMXConnectorServer newJMXConnectorServer(JMXServiceURL address, Map environment, MBeanServer server) throws IOException {
      String protocol = address.getProtocol();

      if (!protocol.equals(PROTOCOL)) {
         throw new MalformedURLException("Protocol not supported " + protocol);
      }
      return newConnectorServer(address, environment, server);
   }

   protected JMXConnectorServer newConnectorServer(JMXServiceURL address, Map environment, MBeanServer server) throws IOException {
      SocketConnectionServer listener = new SocketConnectionServer(address, timeout);

      environment.put(MESSAGE_CONNECTION_SERVER, listener);
      environment.put(OBJECT_WRAPPING, null);

      return new GenericConnectorServer(environment, server);
   }
}
