package com.authrus.common.socket.throttle;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

public class ThrottleRegistryTest extends TestCase {

   public void testRegistry() {
      ThrottleCapacity primary = new ThrottleCapacity(1000);
      ThrottleCapacity fast = new ThrottleCapacity(2000);
      ThrottleCapacity faster = new ThrottleCapacity(3000);
      ThrottleCapacity fastest = new ThrottleCapacity(3000);
      Map<String, ThrottleCapacity> map = new LinkedHashMap<String, ThrottleCapacity>();
      String fastIP = "1.2.*";
      String fasterIP = "5.5.*";
      String fastestIP = "1.2.3.4";

      map.put(fastestIP, fastest);
      map.put(fastIP, fast);
      map.put(fasterIP, faster);

      ThrottleRegistry registry = new ThrottleRegistry(primary, map);

      assertEquals(registry.resolveCapacity("1.2.5.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.4.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.3.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.3.4"), fastest);
      assertEquals(registry.resolveCapacity("5.5.3.4"), faster);

      // check resolution after caching
      assertEquals(registry.resolveCapacity("1.2.5.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.4.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.3.5"), fast);
      assertEquals(registry.resolveCapacity("1.2.3.4"), fastest);
      assertEquals(registry.resolveCapacity("5.5.3.4"), faster);
   }

}
