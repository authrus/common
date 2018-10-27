package com.authrus.gateway.plan;

import java.io.Reader;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import com.zuooh.http.proxy.plan.Plan;
import com.zuooh.http.proxy.plan.deploy.Deployment;

@Configuration
@ComponentScan(basePackageClasses = ProxyPlanConfiguration.class)
public class ProxyPlanConfiguration {

   private final ProxyTraceLogger logger;
   private final ProxyPlanReader reader;
   private final Deployment deployment;
   private final String path;
   
   @SneakyThrows
   public ProxyPlanConfiguration(
         PropertyResolver resolver, 
         @Value("${gateway.plan}") String path,
         @Value("${gateway.debug:false}") boolean debug) 
   {
      this.logger = new ProxyTraceLogger(debug);
      this.deployment = new Deployment(logger);
      this.reader = new ProxyPlanReader(resolver);
      this.path = path;
   }
   
   @Bean
   @SneakyThrows
   public Plan proxyPlan() {
      Reader plan = reader.readPlan(path);
      return deployment.deploy(plan);
   }
}
