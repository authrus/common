package com.authrus.rest;

import com.authrus.rest.container.ContainerManager;
import com.authrus.rest.container.ServiceRegistry;
import com.authrus.rest.container.ServiceRouter;
import com.authrus.rest.manage.ManagementConfiguration;
import com.authrus.rest.registry.RegistryConfiguration;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@Configuration
@Import({RegistryConfiguration.class, ManagementConfiguration.class})
@ComponentScan(basePackageClasses = ResourceServerConfiguration.class)
public class ResourceServerConfiguration {

   private final ResourceServer server;
   private final ServiceRouter router;
   
   public ResourceServerConfiguration(
         @Value("${server.packages:com.authrus.rest}") String packages,
         @Value("${server.name}") String name,
         @Value("${server.directory:.}") File directory,
         @Value("${server.port}") int port,
         @Value("${server.swagger.enabled:true}") boolean swagger)
   {
      this.router = new ServiceRouter();
      this.server = new ResourceServer(router, packages, name, directory, port, swagger);
   }
   
   @Bean
   public ServiceRegistry serviceRegistry() {
      return router;
   }
   
   @Bean
   public ApplicationListener<ApplicationEvent> applicationListener() {
      return (argument) -> {
        try {
           if(ContextRefreshedEvent.class.isInstance(argument)) {
              ContextRefreshedEvent event = (ContextRefreshedEvent)argument;
              ApplicationContext context = event.getApplicationContext();
              ContainerManager endPoint = server.start(context);
              int port = endPoint.getPort();
              
              log.info("Listening on {}", port);
           }
        } catch(Exception e) {
           log.error("Could not launch server", e);
        }
      };
   }
}
