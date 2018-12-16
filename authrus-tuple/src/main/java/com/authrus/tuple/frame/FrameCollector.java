package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameEvent.ERROR;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.Operation;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;

public class FrameCollector implements Operation {

   private final FrameProcessor processor;
   private final ByteCursor cursor;
   private final Channel channel;
   private final Reactor reactor;
   private final Trace trace;

   public FrameCollector(FrameListener listener, FrameTracer tracer, Session session, Channel channel, Reactor reactor) {
      this.processor = new FrameProcessor(listener, tracer, session, channel);
      this.cursor = channel.getCursor();
      this.trace = channel.getTrace();
      this.reactor = reactor;
      this.channel = channel;
   }   
      
   public Trace getTrace() {
      return trace;
   }

   public SelectableChannel getChannel() {
      return channel.getSocket();
   }

   public void run() {
      try {
         processor.process();
         
         if(cursor.isOpen()) {
            reactor.process(this, SelectionKey.OP_READ);
         } else {
            processor.close();
         }
      } catch(Exception cause) {
         trace.trace(ERROR, cause);
         
         try {
            processor.failure(cause);
         } catch(Exception fatal) {
            trace.trace(ERROR, fatal);
         } finally {
            channel.close();
         }
      }
   }

   public void cancel() {
      try{
         processor.close();
      } catch(Exception cause) {
         trace.trace(ERROR, cause);
         channel.close();         
      }
   }      
}
