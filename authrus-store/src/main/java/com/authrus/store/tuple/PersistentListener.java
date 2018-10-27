package com.authrus.store.tuple;

import static com.authrus.store.Reserved.TIME;
import static com.authrus.store.Reserved.VERSION;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import lombok.AllArgsConstructor;

import com.zuooh.tuple.Tuple;
import com.zuooh.tuple.TupleAdapter;
import com.zuooh.tuple.TuplePublisher;

@AllArgsConstructor
class PersistentListener extends TupleAdapter {
   
   private final PersistentEntityBuilder builder;
   private final PersistentEntityStore store;
   private final TuplePublisher publisher;
   private final String[] key;

   @Override
   public void onUpdate(Tuple tuple) {
      Tuple update = publisher.publish(tuple);
      
      if(update != null) {
         store.computeInTransaction((transaction) -> {
            Map<String, Object> attributes = update.getAttributes();
            Comparable<?> value = (Comparable<?>)attributes.get(key[0]);
            Entity entity = builder.create(transaction, value);
            Set<Map.Entry<String, Object>> entries = attributes.entrySet();
            long time = System.currentTimeMillis();
            long version = update.getChange();
            
            for(Map.Entry<String, Object> entry : entries) {
               String column = entry.getKey();
               Comparable<?> attribute = (Comparable<?>)entry.getValue();
               
               if(value == null){
                  if(Objects.equals(column, key)) {
                     throw new IllegalStateException("Primary key is null");
                  }
               } else {
                  entity.setProperty(column, attribute);
               }
            }
            entity.setProperty(TIME, time);
            entity.setProperty(VERSION, version);  
            return entity.getId();
         });
      }
   }
}
