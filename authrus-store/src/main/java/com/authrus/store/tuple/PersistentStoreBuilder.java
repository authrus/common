package com.authrus.store.tuple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jetbrains.exodus.entitystore.PersistentEntityStore;
import lombok.SneakyThrows;

import com.zuooh.tuple.Tuple;
import com.zuooh.tuple.TupleListener;
import com.zuooh.tuple.TuplePublisher;
import com.zuooh.tuple.grid.Catalog;
import com.zuooh.tuple.grid.GridPublisher;
import com.zuooh.tuple.query.Origin;
import com.zuooh.tuple.query.Query;
import com.zuooh.tuple.subscribe.Subscriber;

class PersistentStoreBuilder {
   
   private final TuplePublisher publisher;
   private final Subscriber subscriber;
   private final Origin origin;
   private final String remote;
   
   public PersistentStoreBuilder(Subscriber subscriber, Catalog catalog, Origin origin, String remote) {
      this.publisher = new GridPublisher(catalog);
      this.subscriber = subscriber;
      this.remote = remote;
      this.origin = origin;
   }

   @SneakyThrows
   public TupleStore create(PersistentEntityStore store, String host, String[] key, String name) {
      PersistentEntityBuilder builder = new PersistentEntityBuilder(key, name);
      TupleListener listener = new PersistentListener(builder, store, publisher, key);
      PersistentStore adapter = new PersistentStore(store, listener, key, name);
      Map<String, String> predicates = new LinkedHashMap<>();
      Query query = new Query(origin, predicates);
      
      if(Objects.nonNull(remote) && !remote.isEmpty()) {
         predicates.put(name, "*");
         subscriber.subscribe(listener, query);
      }
      List<Tuple> tuples = adapter.findAll();
      
      for(Tuple tuple : tuples) {
         publisher.publish(tuple); // fill the grid
      }
      return adapter;
   }
}
