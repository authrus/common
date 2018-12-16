package com.authrus.transport.trace;

import java.nio.channels.SelectableChannel;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

import org.simpleframework.transport.trace.Trace;
import org.simpleframework.transport.trace.TraceAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceAgent implements TraceAnalyzer {
   
   private static final Logger LOG = LoggerFactory.getLogger(TraceAgent.class);
   
   private final Set<TraceListener> listeners;
   private final AtomicLong count;

   public TraceAgent() {
      this(Collections.EMPTY_SET);
   }
   
   public TraceAgent(TraceListener listener) {
      this(Collections.singleton(listener));
   }
   
   public TraceAgent(Set<TraceListener> listeners) {
      this.listeners = new CopyOnWriteArraySet<TraceListener>(listeners);
      this.count = new AtomicLong();
   }

   @Override
   public Trace attach(SelectableChannel channel) {     
      return new TraceFeeder(channel);
   }    
   
   public void register(TraceListener listener) {
      listeners.add(listener);
   }
   
   public void remove(TraceListener listener) {
      listeners.remove(listener);
   }

   @Override
   public void stop() {
      listeners.clear();
   }   
   
   private class TraceFeeder implements Trace {
      
      private final SelectableChannel channel;
      private final long sequence;
      
      public TraceFeeder(SelectableChannel channel) {
         this.sequence = count.getAndIncrement();
         this.channel = channel;
      }

      @Override
      public void trace(Object type) {
         trace(type, null);
      }

      @Override
      public void trace(Object type, Object value) {
         if(!listeners.isEmpty()) {
            Thread thread = Thread.currentThread();
            String name = thread.getName();
            
            trace(type, value, name);
         }
      }
      
      private void trace(Object type, Object value, String thread) {
         TraceEvent probe = new TraceEvent(channel, thread, type, value, sequence);
      
         for(TraceListener listener : listeners) {
            try {
               listener.onEvent(probe);
            } catch(Exception e) {
               LOG.info("Error on socket probe", e);
            }
         }         
      }      
   }
}
