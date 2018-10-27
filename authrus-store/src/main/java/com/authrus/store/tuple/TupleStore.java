package com.authrus.store.tuple;

import java.util.List;
import java.util.function.Predicate;

import com.zuooh.tuple.Tuple;

public interface TupleStore {
   void save(Tuple tuple);
   boolean delete(Comparable<?> key);
   boolean delete(String column, Comparable<?> key);
   boolean deleteAll(String column, Comparable<?> value);
   Tuple find(Comparable<?> key);
   Tuple find(String column, Comparable<?> key);
   List<Tuple> findAll();
   List<Tuple> findAll(Predicate<Tuple> filter);
   List<Tuple> findAll(String column, Comparable<?> value);
}
