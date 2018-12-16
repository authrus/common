package com.authrus.message.invoke;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.authrus.tuple.frame.FrameAdapter;
import com.authrus.tuple.queue.InsertSubscriber;
import com.authrus.tuple.queue.QueueServer;

public class InvocationProxy {

   private final InvocationSubscriptionListener listener;
   private final InvocationPublisher publisher;
   private final InsertSubscriber subscriber;
   private final InvocationBroker broker;
   private final InvocationTracer tracer;
   private final InvocationBinder binder;
   private final FrameAdapter adapter;
   private final AtomicBoolean active;
   private final QueueServer server;
   private final String source;

   public InvocationProxy(InvocationTracer tracer, Executor executor, String source, int port) throws Exception {
      this.binder = new InvocationBinder();
      this.adapter = new FrameAdapter();
      this.broker = new InvocationBroker();
      this.subscriber = new InsertSubscriber(executor);
      this.listener = new InvocationSubscriptionListener(binder, tracer, broker, source);
      this.server = new QueueServer(subscriber, listener, adapter, port);
      this.publisher = new InvocationPublisher(binder, broker, subscriber, source);
      this.active = new AtomicBoolean();
      this.tracer = tracer;
      this.source = source;
   }

   public <T> T create(Class<T> type) {
      ClassLoader loader = type.getClassLoader();
      
      if(loader == null) {
         throw new IllegalStateException("Could not find class loader");
      }
      return create(type, loader);
   }
   
   public <T> T create(Class<T> type, ClassLoader loader) {
      InvocationInterceptor interceptor = new InvocationInterceptor(tracer, publisher, type, source);
      Class[] types = new Class[] { type };

      return (T) Proxy.newProxyInstance(loader, types, interceptor);
   }

   public void start() {
      if(!active.get()) {
         active.set(true);
         server.start();
      }
   }

   public void stop() {
      if(active.get()) {
         active.set(false);
         server.stop();
      }
   }
}
