package com.authrus.common.memory;

import junit.framework.TestCase;

public class MemoryUnitTest extends TestCase {
   
   public void testMemoryUnits() throws Exception {
      assertEquals(MemoryUnit.KILOBYTE.toBytes(1), 1024.0);
      assertEquals(MemoryUnit.MEGABYTE.toBytes(1), 1024.0 * 1024.0);
      assertEquals(MemoryUnit.MEGABYTE.toKilobytes(1), 1024.0);    
      assertEquals(MemoryUnit.format(1024), "1.0 KB");
      assertEquals(MemoryUnit.format(2048), "2.0 KB");
      assertEquals(MemoryUnit.format(Integer.MAX_VALUE), "2.0 GB");
   }

}
