package com.authrus.store.resource;

import com.authrus.store.tuple.TupleStore;
import com.authrus.store.tuple.TupleStoreBuilder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import com.zuooh.tuple.Tuple;

@Component
@AllArgsConstructor
public class TupleService {

   private final TupleStoreBuilder builder;
   
   @SneakyThrows
   public List<Tuple> getTuples(String name) {
      Class<?> type = Class.forName(name);
      TupleStore store = builder.create(type);
      
      return store.findAll();
      
   }
   
}
