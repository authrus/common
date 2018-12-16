package com.authrus.common.socket;

import junit.framework.TestCase;

public class HostAddressParserTest extends TestCase {

   public void testAddressParser() throws Exception {
      HostAddressParser parser = new HostAddressParser();
      HostAddress[] addresses = parser.parse("dc1simapp01:2344, dc1simapp02:3456,dc1simapp03:4566 ");

      assertEquals(addresses.length, 3);
      assertEquals(addresses[0].getHost(), "dc1simapp01");
      assertEquals(addresses[0].getPort(), 2344);
      assertEquals(addresses[1].getHost(), "dc1simapp02");
      assertEquals(addresses[1].getPort(), 3456);
      assertEquals(addresses[2].getHost(), "dc1simapp03");
      assertEquals(addresses[2].getPort(), 4566);
   }
}
