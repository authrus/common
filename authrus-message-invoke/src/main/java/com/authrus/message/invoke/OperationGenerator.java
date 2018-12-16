package com.authrus.message.invoke;

import java.util.concurrent.atomic.AtomicLong;

public class OperationGenerator {

   private final AtomicLong counter;
   private final String address;

   public OperationGenerator(String address) {
      this.counter = new AtomicLong();
      this.address = address;
   }

   public String generateOperation() {
      long time = System.currentTimeMillis();
      long count = counter.getAndIncrement();
      
      return address + "#" + count + "@" + time;      
   }
}
