package com.authrus.common.socket;

import junit.framework.TestCase;

public class SocketAcceptorTest extends TestCase {

   public void testLocalPort() throws Exception {
      SocketAcceptor server = new SocketAcceptor(0);
      System.err.println(server.getSocket().getLocalPort());
   }

}
