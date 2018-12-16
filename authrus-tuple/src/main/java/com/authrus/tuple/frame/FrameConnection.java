package com.authrus.tuple.frame;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.common.thread.ConcurrentScheduler;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.transport.TransportBuilder;

class FrameConnection {

   private final AtomicReference<Connection> reference;
   private final ConcurrentScheduler executor;
   private final ConnectionChecker checker;
   private final FrameListener listener;
   private final AtomicBoolean active;
   private final Reactor reactor;

   public FrameConnection(FrameListener listener, FrameTracer tracer, TransportBuilder builder) throws IOException {
      this(listener, tracer, builder, 5000);
   }

   public FrameConnection(FrameListener listener, FrameTracer tracer, TransportBuilder builder, long retry) throws IOException {
      this.executor = new ConcurrentScheduler(FrameCollector.class, 2);
      this.reactor = new ExecutorReactor(executor, 1, retry * 3);
      this.checker = new ConnectionChecker(tracer, builder, reactor, retry);   
      this.reference = new AtomicReference<Connection>();
      this.active = new AtomicBoolean();
      this.listener = listener;
   }   
   
   public synchronized int send(Frame frame) throws IOException {
      Connection connection = reference.get();
      
      if(connection != null) {
         if(active.get()) {
            return connection.send(frame);
         }
      }
      return -1;
   }
   
   public synchronized void connect() throws IOException {
      Connection connection = reference.get();
      
      if(connection == null) {
         if(!active.get()) {
            active.set(true);
            checker.run();
         }
      }
   }   
   
   public synchronized void close() throws IOException {
      Connection connection = reference.get();
      
      if(connection != null) {
         active.set(false);
         reference.set(null);
         connection.close();
      }
   }
   
   private class Connection implements Session {
      
      private final FrameEncoder encoder;
      private final Session session;
      
      public Connection(FrameEncoder encoder, Session session) {
         this.encoder = encoder;
         this.session = session;
      }      
     
      public int send(Frame frame) {
         try {
            return encoder.encode(frame);
         } catch(Exception e) {
            throw new IllegalStateException("Could not send frame on " + session, e);
         }
      }

      @Override
      public boolean isOpen() {
         try {            
            return session.isOpen();
         } catch(Exception e) {
            throw new IllegalStateException("Status not known for " + session, e);
         }
      }

      @Override
      public void ping() {
         try {
            session.ping();
         } catch(Exception e) {
            throw new IllegalStateException("Ping failed for " + session, e);
         }
      }

      @Override
      public void close() {
         try {
            reference.set(null);          
            session.close();
         } catch(Exception e) {
            throw new IllegalStateException("Error during close for " + session, e);                    
         }
      }
      
      @Override
      public String toString() {
         return session.toString();
      }
   }
   
   private class ConnectionChecker implements Runnable {

      private final ConnectionBuilder connector;
      private final TransportBuilder builder;     
      private final FrameTracer tracer;
      private final long retry;

      public ConnectionChecker(FrameTracer tracer, TransportBuilder builder, Reactor reactor, long retry) {
         this.connector = new ConnectionBuilder(tracer, reactor);
         this.builder = builder;
         this.tracer = tracer;
         this.retry = retry;
      }

      @Override
      public void run() {
         try {
            Session session = reference.get();
            
            if(active.get()) {
               try {
                  if(session == null) {
                     connect();
                  } else if(!session.isOpen()) {
                     connect();                     
                  } else {
                     flush();
                  }
               } finally {
                  executor.execute(this, retry);
               }
            }
         } catch (Exception e) {
            listener.onException(e);
         }
      }  
      
      private void flush() {         
         try {
            Session session = reference.get();
            
            if(session != null) {
               tracer.onHeartbeat(session);
               session.ping(); // causes an echo
            }
         } catch(Exception e) {
            listener.onException(e);
         }
      }
     
      private void connect() {
         try {
            Session session = reference.get();
            Transport transport = builder.connect(); 
            String address = builder.toString();
            Operation operation = connector.create(transport, address);
            
            if(session != null) {
               tracer.onClose(session); // close previous
            }
            operation.run();             
         } catch (Exception e) {
            listener.onException(e);
         }
      }
   }

   private class ConnectionNotifier implements Runnable {
      
      private final FrameTracer tracer;
      private final Connection session;
      private final Channel channel;
      private final Runnable task;
      
      public ConnectionNotifier(FrameTracer tracer, Connection session, Channel channel, Runnable task) {
         this.session = session;
         this.channel = channel;
         this.tracer = tracer;
         this.task = task;
      }
      
      @Override
      public void run() {
         try {
            tracer.onConnect(session, channel);
         } finally {
            task.run();
         }
      }
   }
   
   
   private class ConnectionBuilder {      

      private final FrameTracer tracer;
      private final Reactor reactor;

      public ConnectionBuilder(FrameTracer tracer, Reactor reactor) {
         this.reactor = reactor;
         this.tracer = tracer;
      }
      
      public Operation create(Transport transport, String address) throws IOException {
         Channel channel = new TransportChannel(transport);               
         FrameEncoder encoder = new FrameEncoder(channel);
         FrameSession session = new FrameSession(listener, encoder, tracer, channel, address);
         Connection connection = new Connection(encoder, session);
         FrameCollector collector = new FrameCollector(listener, tracer, connection, channel, reactor);
         ConnectionNotifier notifier = new ConnectionNotifier(tracer, connection, channel, collector);
         
         reference.set(connection);                             
         
         return new FrameConnectionFinisher(listener, channel, reactor, notifier);
      } 
   }
}
