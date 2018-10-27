package com.authrus.store;

import static com.authrus.store.Reserved.SOURCE;
import com.authrus.store.tuple.TupleStore;
import com.authrus.store.tuple.TupleStoreBuilder;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import com.google.common.collect.Lists;
import com.zuooh.message.bind.ObjectMarshaller;
import com.zuooh.tuple.Tuple;

public class DataStoreBuilder {
   
   private final TupleStoreBuilder builder;
   private final EntityConverter converter;
   private final SchemaCompiler compiler;
   
   public DataStoreBuilder(TupleStoreBuilder builder, ObjectMarshaller<?> marshaller) {
      this.converter = new EntityConverter(marshaller);
      this.compiler = new SchemaCompiler();
      this.builder = builder;
   }

   @SneakyThrows
   public <T> DataStore<T> create(Class<T> type) {
      TupleStore store = builder.create(type);
      Schema<T> schema = compiler.compile(type);
      InetAddress address = InetAddress.getLocalHost();
      String host = address.getCanonicalHostName();
      
      return new PersistentDataStore<>(converter, store, schema, host);
   }
   
   @AllArgsConstructor
   private static class PersistentDataStore<T> implements DataStore<T> {
      
      private final EntityConverter converter;
      private final TupleStore store;
      private final Schema<T> schema;
      private final String host;

      @Override
      public void save(T object) {
         Tuple tuple = converter.fromEntity(object, schema);
         Map<String, Object> attributes = tuple.getAttributes();
         
         attributes.put(SOURCE, host);
         store.save(tuple);
      }

      @Override
      public boolean delete(Comparable<?> value) {
         return store.delete(value);
      }

      @Override
      public boolean delete(String column, Comparable<?> value) {
         return store.delete(column, value);
      }
      
      @Override
      public boolean deleteAll(String column, Comparable<?> value) {
         return store.deleteAll(column, value);
      }

      @Override
      public T find(Comparable<?> value) {
         Tuple tuple = store.find(value);
         
         if(tuple != null) {
            return (T)converter.toEntity(tuple);
         }
         return null;
      }
      
      @Override
      public T find(String column, Comparable<?> value) {
         Tuple tuple = store.find(column, value);
         
         if(tuple != null) {
            return (T)converter.toEntity(tuple);
         }
         return null;
      }

      @Override
      public List<T> findAll() {
         return findAll((object) -> true);
      }

      @Override
      public List<T> findAll(Predicate<T> filter) {
         List<T> result = Lists.newArrayList();
         
         store.findAll((tuple) -> {
            T value = (T)converter.toEntity(tuple);
            
            if(filter.test(value)) {
               result.add(value);
               return true;
            }
            return false;
         });
         return result;
      }

      @Override
      public List<T> findAll(String column, Comparable<?> value) {
         List<T> result = Lists.newArrayList();
         List<Tuple> tuples = store.findAll(column, value);
         
         for(Tuple tuple : tuples) {
            T object = (T)converter.toEntity(tuple);
            result.add(object);
         }
         return result;
      }
      
   }
}
