package com.authrus.tuple.grid;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.common.thread.ConcurrentExecutor;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.TransportSocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.tuple.frame.FrameCollector;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.subscribe.SubscriptionListener;

public class GridServer {
   
   private final ConcurrentExecutor executor;
   private final GridProcessor processor;
   private final SocketProcessor adapter;
   private final SocketAddress address;
   private final Connection connection;
   private final Reactor reactor;

   public GridServer(ChangeSubscriber subscriber, SubscriptionListener listener, FrameTracer tracer, int port) throws IOException {
      this.executor = new ConcurrentExecutor(FrameCollector.class, 10);
      this.reactor = new ExecutorReactor(executor);
      this.processor = new GridProcessor(subscriber, listener, tracer, reactor);
      this.adapter = new TransportSocketProcessor(processor, 10);
      this.connection = new SocketConnection(adapter); 
      this.address = new InetSocketAddress(port);
   }

   public int start() {
      try {
         SocketAddress local = connection.connect(address);
         InetSocketAddress value = (InetSocketAddress)local;  

         processor.start();
         
         return value.getPort();
      } catch(Exception e) {
         throw new IllegalStateException("Could not start server on " + address, e);
      }
   }

   public void stop() {
      try {
         processor.stop();
         connection.close();
      } catch(Exception e) {
         throw new IllegalStateException("Could not stop server", e);
      }
   }
}
