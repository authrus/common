package com.authrus.store.tuple;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LazyBuilder;
import com.authrus.common.collections.LazyCache;
import com.authrus.store.Schema;
import com.authrus.store.SchemaCompiler;

import java.io.File;
import java.net.InetAddress;

import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import lombok.SneakyThrows;

import com.zuooh.tuple.grid.Catalog;
import com.zuooh.tuple.grid.Grid;
import com.zuooh.tuple.grid.Structure;
import com.zuooh.tuple.query.Origin;
import com.zuooh.tuple.subscribe.Subscriber;

public class TupleStoreBuilder {
   
   private final Cache<Class<?>, TupleStore> cache;
   private final PersistentStoreLoader loader;

   public TupleStoreBuilder(Subscriber subscriber, Catalog catalog, Origin origin, File path) {
      this(subscriber, catalog, origin, path, null);
   }
   
   public TupleStoreBuilder(Subscriber subscriber, Catalog catalog, Origin origin, File path, String remote) {
      this.loader = new PersistentStoreLoader(subscriber, catalog, origin, path, remote);
      this.cache = new LazyCache<>(loader);
   }
   
   public TupleStore create(Class<?> type) {
      return cache.fetch(type);
   }
   
   private static class PersistentStoreLoader implements LazyBuilder<Class<?>, TupleStore> {
   
      private final PersistentStoreBuilder builder;
      private final SchemaCompiler compiler;
      private final Catalog catalog;
      private final Origin origin;
      private final File path;
      
      public PersistentStoreLoader(Subscriber subscriber, Catalog catalog, Origin origin, File path, String remote) {
         this.builder = new PersistentStoreBuilder(subscriber, catalog, origin, remote);
         this.compiler = new SchemaCompiler();
         this.catalog = catalog;
         this.origin = origin;
         this.path = path;
      }
      
      @Override
      @SneakyThrows
      public TupleStore create(Class<?> type) {
         String name = type.getSimpleName();
         String root = path.getCanonicalPath();
         PersistentEntityStore store = PersistentEntityStores.newInstance(root + "/" + name);
         String host = origin.getHost();
         Schema<?> schema = compiler.compile(type);
         Structure structure = schema.getStructure();
         String[] key = structure.getKey();
         Grid grid = catalog.getGrid(name);
         
         if(grid == null) {
            throw new IllegalArgumentException("Grid not found for " + type);
         }
         if(!path.exists()) {
            path.mkdirs();
         }
         return builder.create(store, host, key, name);
      }
   }
}
