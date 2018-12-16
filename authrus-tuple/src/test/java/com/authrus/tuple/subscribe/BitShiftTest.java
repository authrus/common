package com.authrus.tuple.subscribe;

import junit.framework.TestCase;

public class BitShiftTest extends TestCase {
   public void testBitShift() {
      int x = 523592743;
      byte[] octets = new byte[4];
      octets[0] = (byte) ((x >>> 24) & 0xff);
      octets[1] = (byte) ((x >>> 16) & 0xff);
      octets[2] = (byte) ((x >>> 8) & 0xff);
      octets[3] = (byte) ((x >>> 0) & 0xff);
      System.err.println(octets[0]);
      System.err.println(octets[1]);
      System.err.println(octets[2]);
      System.err.println(octets[3]);
      int val = 0;
      val <<= 8;
      val |= octets[0] & 0xff;
      val <<= 8;
      val |= octets[1] & 0xff;
      val <<= 8;
      val |= octets[2] & 0xff;
      val <<= 8;
      val |= octets[3] & 0xff;
      System.err.println(val);
   }

}
