package com.authrus.store;

import java.util.List;
import java.util.function.Predicate;

public interface DataStore<T> {
   void save(T object);
   boolean delete(Comparable<?> key);
   boolean delete(String column, Comparable<?> value);
   boolean deleteAll(String column, Comparable<?> value);
   T find(Comparable<?> key);
   T find(String column, Comparable<?> key);
   List<T> findAll();
   List<T> findAll(Predicate<T> filter);
   List<T> findAll(String column, Comparable<?> value);
}
