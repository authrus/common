package com.authrus.tuple.grid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeltaValidator {

   private final Map<String, State> states;

   public DeltaValidator() {    
      this.states = new HashMap<String, State>();
   }
   
   public void validate(Delta delta, Schema schema) {
      String type = delta.getType();
      State state = states.get(type);
      
      if(state == null) {
         state = new State(type);
         states.put(type, state);
      }
      validate(delta, schema, state);
   }
   
   private void validate(Delta delta, Schema schema, State state) {
      TableDelta table = delta.getTable();
      List<RowDelta> changes = table.getChanges();
      KeyDelta indexes = table.getKeys();

      if (indexes != null) {
         validate(indexes, state);
      }
      if(changes != null) {
         Iterator<RowDelta> iterator = changes.iterator();
         
         while(iterator.hasNext()) {
            RowDelta row = iterator.next();
            Key key = row.getKey();
            Long previous = state.getChange(key);
            
            if(previous != null) {
               Long current = row.getChange();
               
               if(previous.equals(current)) {
                  iterator.remove(); // remove double updates!!
               }  
            }
         }
         for (RowDelta row : changes) {
            validate(row, schema, state);
         }
      }
   }

   private void validate(KeyDelta delta, State state) {
      List<Key> changes = delta.getChanges();

      for (Key key : changes) {
         Version version = key.getVersion();
         Long previous = state.getVersion(key);
         Long current = version.get();         
         String name = key.getName();
         
         if(previous != null) {
            if (!previous.equals(current)) {  
               state.setChange(key, null);            
            }
         }
         state.setVersion(key, current);
         state.setName(key, name);                 
      }
   }

   private void validate(RowDelta delta, Schema schema, State state) {
      Key key = delta.getKey();
      String name = state.getName(key);
      long change = delta.getChange();
      int row = key.getIndex();

      if (name == null) {         
         throw new IllegalStateException("Row at index " + row + " does not have a key for " + delta);
      }
      List<Cell> cells = delta.getChanges();
      Long previous = state.getChange(key);
      
      if(previous == null) {
         int columns = delta.getColumns();
         int count = cells.size();

         if (columns != count) {
            throw new IllegalStateException("New row '" + name + "' only has " + count + " of expected " + columns + " changes in " + delta);
         }         
      } else {
         if(previous > change) {      
            throw new IllegalStateException("Change count " + change + " for row '" + name + "' is lower than previous count");
         }
      }   
      state.setChange(key, change);     
   }
   
   private static class State {
      
      private Long[] versions;
      private Long[] changes;
      private String[] keys;
      private String type;
      
      public State(String type) {
         this.versions = new Long[0];
         this.changes = new Long[0];
         this.keys = new String[0];
         this.type = type;
      }
      
      public void setName(Key key, String name) {
         int row = key.getIndex();
         
         if(row >= keys.length) {
            keys = Arrays.copyOf(keys, row + 1);
         }         
         keys[row] = name;
      }
      
      public String getName(Key key) {
         int row = key.getIndex();
         
         if (row < keys.length) {
            return keys[row];
         }
         return null;
      }
      
      public void setChange(Key key, Long change) {
         int row = key.getIndex();
         
         if(row >= changes.length) {
            changes = Arrays.copyOf(changes, row + 1);
         }
         changes[row] = change;
      }
      
      public Long getChange(Key key) {
         int row = key.getIndex();
         
         if (row < changes.length) {
            return changes[row];
         }
         return null;
      }  
      
      public void setVersion(Key key, Long version) {
         int row = key.getIndex();
         
         if(row >= versions.length) {
            versions = Arrays.copyOf(versions, row + 1);
         }
         versions[row] = version;
      }
      
      public Long getVersion(Key key) {
         int row = key.getIndex();
         
         if (row < versions.length) {
            return versions[row];
         }
         return null;
      } 
      
      @Override
      public String toString() {
         return type;
      }
   }
}
