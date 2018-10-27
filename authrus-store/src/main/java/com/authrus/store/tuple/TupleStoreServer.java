package com.authrus.store.tuple;

import com.authrus.common.AnnotationPresentScanner;
import com.authrus.common.ClassPathScanner;
import com.authrus.store.Entity;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import lombok.SneakyThrows;

import com.zuooh.tuple.frame.SessionRegistry;
import com.zuooh.tuple.grid.Catalog;
import com.zuooh.tuple.subscribe.SubscriptionListener;

public class TupleStoreServer {
   
   private final TupleStoreSubscriber subscriber;
   private final ClassPathScanner scanner;
   private final CatalogBuilder builder;
   
   public TupleStoreServer(List<SubscriptionListener> listeners, File path, String packages, String name, String remote, int port) {
      this.scanner = new AnnotationPresentScanner(Entity.class, "", packages);
      this.builder = new CatalogBuilder(listeners, port);
      this.subscriber = new TupleStoreSubscriber(path, name, remote, port);
   }
   
   @SneakyThrows
   public TupleStoreBuilder start(SessionRegistry registry) {
      Set<Class<?>> matches = scanner.scan();
      Catalog catalog = builder.create(registry, matches);
      InetAddress address = InetAddress.getLocalHost();
      String host = address.getCanonicalHostName();
      
      return subscriber.subscribe(registry, catalog, host);
   }
}
