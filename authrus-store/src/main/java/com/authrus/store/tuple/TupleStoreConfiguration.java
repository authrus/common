package com.authrus.store.tuple;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zuooh.tuple.frame.SessionRegistry;
import com.zuooh.tuple.subscribe.SubscriptionListener;

@Configuration
public class TupleStoreConfiguration {

   private final SessionRegistry registry;
   private final TupleStoreServer server;
   
   public TupleStoreConfiguration(
         List<SubscriptionListener> listeners,
         @Value("${tuple.path}") File path,
         @Value("${tuple.packages}") String packages,
         @Value("${tuple.name}") String name,
         @Value("${tuple.remote:}") String remote,
         @Value("${tuple.port}") int port)
   {
      this.server = new TupleStoreServer(listeners, path, packages, name, remote, port);
      this.registry = new SessionRegistry();
   }
   
   @Bean
   public SessionRegistry sessionRegistry() {
      return registry;
   }
   
   @Bean
   public TupleStoreBuilder tupleStoreBuilder() {
      return server.start(registry);
   }
}
