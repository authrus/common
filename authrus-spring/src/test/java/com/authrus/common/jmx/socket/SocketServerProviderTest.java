package com.authrus.common.jmx.socket;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;

import junit.framework.TestCase;

import com.authrus.common.manage.jmx.socket.SocketClientProvider;
import com.authrus.common.manage.jmx.socket.SocketServerProvider;

public class SocketServerProviderTest extends TestCase {

   public void testProvider() throws Exception {
      MBeanServer mbs = MBeanServerFactory.createMBeanServer();
      HashMap serverEnvironment = new HashMap();
      JMXServiceURL url = new JMXServiceURL("p2p", null, 5555);
      JMXConnectorServerProvider serverProvider = new SocketServerProvider(60000);
      JMXConnectorServer server = serverProvider.newJMXConnectorServer(url, serverEnvironment, mbs);

      server.start();

      HashMap clientEnvironment = new HashMap();
      JMXConnectorProvider clientProvider = new SocketClientProvider(60000);
      JMXConnector client = clientProvider.newJMXConnector(url, clientEnvironment);

      client.connect();
      client.close();

      Method getProviderIterator = JMXConnectorFactory.class.getDeclaredMethod("getProviderIterator", Class.class, ClassLoader.class);

      getProviderIterator.setAccessible(true);

      Iterator providers = (Iterator) getProviderIterator.invoke(null, JMXConnectorServerProvider.class, SocketServerProviderTest.class.getClassLoader());

      assertNotNull(providers);

      while (providers.hasNext()) {
         System.err.println(providers.next());
      }

      Method getConnectorServerAsService = JMXConnectorServerFactory.class.getDeclaredMethod("getConnectorServerAsService", ClassLoader.class, JMXServiceURL.class, Map.class, MBeanServer.class);

      getConnectorServerAsService.setAccessible(true);

      Object result = getConnectorServerAsService.invoke(null, SocketServerProviderTest.class.getClassLoader(), url, serverEnvironment, mbs);

      assertNotNull(result);

      JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnvironment, mbs);

      assertNotNull(cs);

      server.stop();
      cs.start();
   }
}
