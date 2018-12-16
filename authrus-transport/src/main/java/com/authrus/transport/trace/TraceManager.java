package com.authrus.transport.trace;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TraceManager {
   
   private final Set<TraceListener> listeners;
   private final TraceAgent agent;

   public TraceManager(TraceAgent agent) {
      this(agent, Collections.EMPTY_SET);
   }

   public TraceManager(TraceAgent agent, TraceListener listener) {
      this(agent, Collections.singleton(listener));      
   }
   
   public TraceManager(TraceAgent agent, Set<TraceListener> listeners) {
      this.listeners = new CopyOnWriteArraySet<TraceListener>(listeners);   
      this.agent = agent;
   }   
   
   public void register(TraceListener listener) {
      listeners.add(listener);
   }
   
   public void remove(TraceListener listener) {
      listeners.remove(listener);
   }
   
   public void start() throws IOException {
      for(TraceListener listener : listeners) {
         agent.register(listener);
      }
   }
   
   public void stop() throws IOException {
      for(TraceListener listener : listeners) {
         agent.remove(listener);
      }
   }     
}
