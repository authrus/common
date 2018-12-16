package com.authrus.message.invoke;

import java.util.concurrent.atomic.AtomicInteger;

public class ExampleService implements ExampleServiceInterface {
   
   public final AtomicInteger counter;
   public final String serviceName;
   
   public ExampleService(String serviceName, AtomicInteger counter) {
      this.serviceName = serviceName;
      this.counter = counter;
   }

   @Override
   public String invokeBlah(String name, int value) {
      counter.getAndIncrement();
      return serviceName + ".invokeBlah(" + name +", " + value +")";
   }      
}
