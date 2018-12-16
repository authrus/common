package com.authrus.tuple.grid;

import com.authrus.tuple.grid.Key;
import com.authrus.tuple.grid.KeyAllocator;
import com.authrus.tuple.grid.Revision;
import com.authrus.tuple.grid.Version;

import junit.framework.TestCase;


public class KeyAllocatorTest extends TestCase {
   
   public void testKeyAllocator() {
      Version version = new Version();
      Revision revision = new Revision(version);
      KeyAllocator allocator = new KeyAllocator(revision, 200);
      
      for(int i = 0; i < 10000; i++) {
         String value="key-"+i;
         Key key = allocator.getKey(value);
         int expect = i % 600;
         
         if(i % 10000 == 0) {
            System.err.println(i);
         }
         assertEquals("Error on " + expect + " expected " + key.getIndex() + " at " + i, key.getIndex(),expect);
      }      
   }

}
