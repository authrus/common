package com.authrus.rest.container;

import com.authrus.rest.swagger.SwaggerPathLogger;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Provider;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import lombok.extern.slf4j.Slf4j;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.inject.hk2.ImmediateHk2InjectionManager;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class DependencyManager {

   private final SwaggerPathLogger logger;
   private final ResourceScanner scanner;
   private final boolean swagger;
   
   public DependencyManager(String packages, boolean swagger) {
      this.scanner = new ResourceScanner(packages);
      this.logger = new SwaggerPathLogger();
      this.swagger = swagger;
   }
   
   public ResourceConfig start(ConfigurableApplicationContext context) {
      Set<Class<?>> types = scanner.scan(swagger);
      String component = ResourceScanner.class.getSimpleName();
      ConfigurableListableBeanFactory factory = context.getBeanFactory();
      
      try {
         ResourceConfig config = new ResourceConfig(types);
         ServiceLocatorFeature feature = new ServiceLocatorFeature(context);
         
         for(Class type : types) {
            log.info("Loading {}", type);
         }
         factory.registerSingleton(component, scanner);
         config.register(feature);
         logger.log();
         
         return config;
      } catch(Exception e) {
         throw new IllegalStateException("Could not create container", e);
      }
   }
   
   private static class ServiceLocatorFeature implements Feature, Provider<ServiceLocator> {
      
      private final AtomicReference<ServiceLocator> reference;
      private final ConfigurableApplicationContext context;
      
      public ServiceLocatorFeature(ConfigurableApplicationContext context) {
         this.reference = new AtomicReference<ServiceLocator>();
         this.context = context;
      }
      
      @Override
      public ServiceLocator get() {
         ServiceLocator locator = reference.get();
         
         if(locator == null) {
            throw new IllegalStateException("Service locator is not yet available");
         }
         return locator;
      }
      
      @Override
      public boolean configure(FeatureContext feature) {
         ImmediateHk2InjectionManager manager = (ImmediateHk2InjectionManager) InjectionManagerProvider.getInjectionManager(feature);
         ServiceLocator locator = manager.getServiceLocator();
         BeanFactory factory = context.getBeanFactory();
         
         SpringBridge.getSpringBridge().initializeSpringBridge(locator);
         SpringIntoHK2Bridge springBridge = locator.getService(SpringIntoHK2Bridge.class);
         springBridge.bridgeSpringBeanFactory(factory);
         reference.set(locator);
         
         return true;
      }
   }
}
