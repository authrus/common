package com.authrus.common.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FixedLengthQueueTest {
   
   @Test
   public void checkQueueCapacity() {
      FixedLengthQueue<String> queue = new FixedLengthQueue<String>(10);
      
      for(int i = 0; i < 20; i++) {
         queue.offer(String.valueOf(i));
      }
      assertEquals(queue.size(), 10);
      
      for(int i = 10; i < 20; i++) {
         assertTrue(queue.contains(String.valueOf(i)));         
      }
      System.err.println(queue);
   }

}
