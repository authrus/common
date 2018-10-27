package com.authrus.store.tuple;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class PersistentEntityBuilder {
   
   private final String[] key;
   private final String name;

   public Entity create(StoreTransaction transaction, Comparable<?> value) {
      if(value != null) {
         EntityIterable iterable = transaction.find(name, key[0], value);
         
         if(!iterable.isEmpty()) {
            return iterable.getFirst();
         }
      }
      return transaction.newEntity(name);
   }
}
