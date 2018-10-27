package com.authrus.rest.registry;

import com.authrus.common.zookeeper.ZooKeeperClient;
import com.authrus.common.zookeeper.ZooKeeperConfiguration;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
@Import(ZooKeeperConfiguration.class)
public class RegistryConfiguration {

   private final ScheduledThreadPoolExecutor executor;
   private final ZooKeeperClient client;
   private final ObjectMapper mapper;
   private final String environment;
   private final String name;
   private final File path;
   private final int port;
   
   public RegistryConfiguration(
         ZooKeeperClient client, 
         @Value("${server.name}") String name,
         @Value("${server.environment}") String environment,
         @Value("${server.directory}") File path,
         @Value("${server.port}") int port)
   {
      this.executor = new ScheduledThreadPoolExecutor(1);
      this.mapper = new ObjectMapper();
      this.environment = environment;
      this.client = client;
      this.name = name;
      this.port = port;
      this.path = path;
   }
   
   @Bean
   public ApplicationListener<ApplicationEvent> registryListener(Registry registry) {
      return (event) -> {
        try {
           if(ContextRefreshedEvent.class.isInstance(event)) {
              InetAddress address = InetAddress.getLocalHost();
              String host = address.getCanonicalHostName();
              String directory = path.getCanonicalPath();
              String location = String.format("http://%s:%s/", host, port);
              RegistryNode node = RegistryNode.builder()
                    .name(name)
                    .address(location)
                    .directory(directory)
                    .environment(environment)
                    .host(host)
                    .build();
              
              executor.scheduleAtFixedRate(() -> registry.addNode(name, node), 1, 10, TimeUnit.SECONDS);
              log.info("Registering service {} at {}", name, location);
           }
        } catch(Exception e) {
           log.error("Could not register service", e);
        }
      };
   }
   
   @Bean
   @SneakyThrows
   public Registry registry() {
      InetAddress address = InetAddress.getLocalHost();
      String host = address.getCanonicalHostName();
      
      return new Registry(client, mapper, environment, host);
   }
}
