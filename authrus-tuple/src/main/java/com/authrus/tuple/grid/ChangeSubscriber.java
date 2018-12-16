package com.authrus.tuple.grid;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeSubscriber implements ChangeListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(ChangeSubscriber.class);

   private final Map<String, ChangeDispatcher> dispatchers;
   private final Map<String, ChangeState> changes;
   private final Set<String> addresses;
   private final Set<String> types;
   private final Executor executor;
   
   public ChangeSubscriber(Executor executor) {
      this.dispatchers = new ConcurrentHashMap<String, ChangeDispatcher>();
      this.changes = new ConcurrentHashMap<String, ChangeState>();
      this.addresses = new CopyOnWriteArraySet<String>();
      this.types = new CopyOnWriteArraySet<String>();
      this.executor = executor;
   }

   public void subscribe(String address, ChangeListener listener) {
      ChangeDispatcher dispatcher = new ChangeDispatcher(listener, executor);

      dispatchers.put(address, dispatcher);
      addresses.add(address);
   }

   public void cancel(String address) {
      ChangeDispatcher dispatcher = dispatchers.remove(address);

      if (dispatcher != null) {
         addresses.remove(address);
      }
   }
   
   public void flush(String address) {
      for(String type : types) {
         ChangeState state = changes.get(type);
         Schema schema = state.getSchema();
         Grid grid = state.getGrid();
         
         onChange(grid, schema, type, address);
      }
   }

   @Override
   public void onChange(Grid grid, Schema schema, String type) {
      ChangeState state = new ChangeState(grid, schema);
      
      try {
         for (String address : addresses) {
            onChange(grid, schema, type, address);
         }    
      } finally {
         if(!types.contains(type)) {         
            changes.put(type, state);
            types.add(type);         
         }
      }
   }
   
   private void onChange(Grid grid, Schema schema, String type, String address) {
      ChangeDispatcher dispatcher = dispatchers.get(address);
      
      try {
         if (dispatcher != null) {
            dispatcher.onChange(grid, schema, type);
         } else {
            addresses.remove(address);
         }
      } catch (Exception e) {
         LOG.info("Change for '" + type + "' could not be dispatched to '" + address + "'", e);
         
         addresses.remove(address);
         dispatchers.remove(address);         
      }
   }

   private class ChangeState {

      private final Schema schema;
      private final Grid grid;

      public ChangeState(Grid grid, Schema schema) {
         this.schema = schema;
         this.grid = grid;
      }

      public Grid getGrid() {
         return grid;
      }

      public Schema getSchema() {
         return schema;
      }
   }
}
