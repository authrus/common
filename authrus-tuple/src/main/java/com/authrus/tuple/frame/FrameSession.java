package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameType.HEARTBEAT;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.transport.Channel;

import com.authrus.tuple.query.Query;

public class FrameSession implements Session {

   private final AtomicReference<Query> subscription;
   private final AtomicReference<String> address;
   private final FrameController controller; 
   private final FrameEncoder encoder; 
   private final SocketChannel socket;
   private final AtomicBoolean active;
   private final Channel channel;
   private final Frame empty;

   public FrameSession(FrameListener listener, FrameEncoder encoder, FrameTracer tracer, Channel channel) {
      this(listener, encoder, tracer, channel, null);
   }
   
   public FrameSession(FrameListener listener, FrameEncoder encoder, FrameTracer tracer, Channel channel, String address) {
      this.controller = new FrameController(this, listener, tracer);
      this.address = new AtomicReference<String>(address);
      this.subscription = new AtomicReference<Query>();
      this.active = new AtomicBoolean(true);
      this.empty = new Frame(HEARTBEAT);
      this.socket = channel.getSocket();
      this.encoder = encoder;
      this.channel = channel;
   }   
   
   public void update(Query query) {
      try {         
         subscription.set(query);        
      } catch(Exception cause) {
         controller.failure(cause);
         controller.dispose();
      }      
   }
   
   @Override
   public boolean isOpen() {
      try {
         if(active.get()) {
            return socket.isConnected();
         }
      } catch(Exception cause) {
         controller.failure(cause);
         controller.dispose();
      }
      return false;
   }   

   @Override
   public void ping() {
      try {
         if(isOpen()) { 
            encoder.encode(empty);
         }
      } catch(Exception cause) {
         controller.failure(cause);
         controller.dispose();
      }
   }
   
   @Override
   public void close() {
      try {
         controller.close();
      } catch(Exception cause) {         
         try {
            controller.dispose();
         } catch(Exception fatal) {
            controller.failure(fatal);
         }
      }
   }
   
   @Override
   public String toString() {
      return String.valueOf(address);
   }
   
   private class FrameController {
      
      private final FrameListener listener; 
      private final FrameSession session;
      private final FrameTracer tracer;
      
      public FrameController(FrameSession session, FrameListener listener, FrameTracer tracer) {
         this.listener = listener;
         this.session = session;
         this.tracer = tracer;
      }
      
      public void failure(Exception cause) {
         tracer.onException(session, cause);
         listener.onException(cause);
      }    
      
      public void close() {
         active.set(false);
         tracer.onClose(session);
         listener.onClose();
         channel.close();
      }      
      
      public void dispose() {
         active.set(false);
         channel.close();
      }        
   }
}
