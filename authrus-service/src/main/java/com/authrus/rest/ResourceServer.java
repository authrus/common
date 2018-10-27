package com.authrus.rest;

import com.authrus.rest.container.ContainerManager;
import com.authrus.rest.container.ContainerManagerBuilder;
import com.authrus.rest.container.DependencyManager;
import com.authrus.rest.container.ServiceRouter;
import com.authrus.rest.content.ContentHandlerMatcher;
import com.authrus.rest.content.FileContentHandlerMatcher;

import java.io.File;

import lombok.SneakyThrows;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class ResourceServer {
   
   private final ContainerManagerBuilder builder;
   private final ContentHandlerMatcher matcher;
   private final DependencyManager manager;
   private final int port;
   
   public ResourceServer(ServiceRouter router, String packages, String name, File directory, int port, boolean swagger) {
      this.matcher = new FileContentHandlerMatcher(directory);
      this.manager = new DependencyManager(packages, swagger);
      this.builder = new ContainerManagerBuilder(matcher, router, name);
      this.port = port;
   }

   @SneakyThrows
   public ContainerManager start(ApplicationContext context) {
      ResourceConfig config = manager.start((ConfigurableApplicationContext)context);
      return builder.create(config, null, port);
   }
}
