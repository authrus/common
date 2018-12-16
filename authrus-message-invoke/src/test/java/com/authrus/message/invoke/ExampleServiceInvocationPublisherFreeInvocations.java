package com.authrus.message.invoke;

import java.util.concurrent.Executor;

import com.authrus.common.thread.ThreadPool;

public class ExampleServiceInvocationPublisherFreeInvocations {
   
   private final String name;
   private final int port;
   
   public ExampleServiceInvocationPublisherFreeInvocations(String name, int port) {
      this.name = name;
      this.port = port;
   }  

   public ExampleServiceInterface create() throws Exception {      
      InvocationTracer tracer = new InvocationAdapter() {
         @Override
         public void onException(Exception cause) {
            cause.printStackTrace();
         }
      };
      Executor executor = new ThreadPool(2);
      InvocationProxy builder = new InvocationProxy(tracer, executor, name, port); 
      ExampleServiceInterface proxy = builder.create(ExampleServiceInterface.class);
      
      builder.start();

      return proxy;
   }
   
   public static void main(String[] list) throws Exception {
      ExampleServiceInvocationPublisherFreeInvocations publisher = new ExampleServiceInvocationPublisherFreeInvocations("client", 19778);
      ExampleServiceInterface service = publisher.create();
      
      Thread.sleep(10000); // Wait for subscribers
      
      for(int i = 0; i < 100000; i++) {
         String value = service.invokeBlah("some_invocation " + i, i);
         System.err.println(value);         
      }
   }
}
