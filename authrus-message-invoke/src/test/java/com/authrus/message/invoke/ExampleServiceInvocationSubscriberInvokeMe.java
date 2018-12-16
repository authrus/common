package com.authrus.message.invoke;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadDumper;
import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;

public class ExampleServiceInvocationSubscriberInvokeMe {
   
   private final AtomicReference<InvocationConnection> reference;
   private final ExampleService service;
   private final String name;
   private final int port;
   
   public ExampleServiceInvocationSubscriberInvokeMe(ExampleService service, String name, int port) {
      this.reference = new AtomicReference<InvocationConnection>();
      this.service = service;      
      this.name = name;
      this.port = port;
   }
   
   public void start() throws Exception {
      InvocationConnection connection = createNewService(name, port);
      
      if(connection != null) {
         connection.connect();    
      }
   }
   
   public void stop() throws Exception {
      InvocationConnection connection = reference.get();
      
      if(connection != null) {
         connection.close();
      }
   }
   
   private InvocationConnection createNewService(String serviceName, int connectToPort) throws Exception {
      Map<Class, Object> services = new HashMap<Class, Object>();

      services.put(ExampleServiceInterface.class, service);

      InvocationTracer tracer = new InvocationAdapter() {
         @Override
         public void onException(Exception cause) {
            cause.printStackTrace();
         }
      };
      InvocationDispatcher dispatcher = new InvocationDispatcher(services);
      TraceAgent analyzer = new TraceAgent();
      DirectSocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", connectToPort);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      ///SocketEventLogger l = new SocketEventLogger(analyzer);
      //l.start();
      InvocationConnector connector = new InvocationConnector(tracer, dispatcher, transportBuilder, name);
      InvocationConnection connection = new InvocationConnection(connector);

      return connection;
   }
   
   public static void main(String[] list) throws Exception {
      Thread thread = new Thread() {
         public void run(){
            try {
               while(true) {
                  Thread.sleep(5000);
                  System.err.println(new ThreadDumper().dumpThreads());
               }
            } catch(Exception e){
               e.printStackTrace();
            }            
         }         
      };
      AtomicInteger masterCounter = new AtomicInteger();
      AtomicInteger slaveCounter = new AtomicInteger();
      ExampleService masterService = new ExampleService("master", masterCounter);
      ExampleService slaveService = new ExampleService("slave", slaveCounter);
      ExampleServiceInvocationSubscriberInvokeMe masterReceiver = new ExampleServiceInvocationSubscriberInvokeMe(masterService, "master", 19778);
      ExampleServiceInvocationSubscriberInvokeMe slaveReceiver = new ExampleServiceInvocationSubscriberInvokeMe(slaveService, "slave", 19778);      
      
      masterReceiver.start();
      slaveReceiver.start();   
   }
}
