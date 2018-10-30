package com.authrus.gateway.plan;

import java.io.Reader;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import com.authrus.gateway.deploy.DeploymentEngine;
import com.authrus.gateway.deploy.Deployment;

@Configuration
@ComponentScan(basePackageClasses = ProxyPlanConfiguration.class)
public class ProxyPlanConfiguration {

   private final ProxyTraceLogger logger;
   private final ProxyPlanReader reader;
   private final DeploymentEngine deployment;
   private final String path;
   
   @SneakyThrows
   public ProxyPlanConfiguration(
         PropertyResolver resolver, 
         @Value("${gateway.plan}") String path,
         @Value("${gateway.debug:false}") boolean debug) 
   {
      this.logger = new ProxyTraceLogger(debug);
      this.deployment = new DeploymentEngine(logger);
      this.reader = new ProxyPlanReader(resolver);
      this.path = path;
   }
   
   @Bean
   @SneakyThrows
   public Deployment proxyPlan() {
      Reader plan = reader.readPlan(path);
      return deployment.deploy(plan);
   }
}
