package com.authrus.gateway.deploy;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import com.authrus.gateway.deploy.parse.SourceReader;
import com.authrus.gateway.deploy.trace.TraceDistributor;
import com.zuooh.http.proxy.trace.TraceCollector;

@Configuration
@ComponentScan(basePackageClasses = DeploymentConfiguration.class)
public class DeploymentConfiguration {

   private final TraceDistributor tracer;
   private final SourceReader reader;
   private final DeploymentEngine engine;
   private final String path;
   
   @SneakyThrows
   public DeploymentConfiguration(
		 Optional<List<TraceCollector>> collectors,  
         PropertyResolver resolver, 
         @Value("${gateway.plan}") String path,
         @Value("${gateway.debug:false}") boolean debug) 
   {
      this.tracer = new TraceDistributor(collectors);
      this.engine = new DeploymentEngine(tracer);
      this.reader = new SourceReader(resolver);
      this.path = path;
   }
   
   @Bean
   @SneakyThrows
   public Deployment proxyPlan() {
      Reader source = reader.readDeployment(path);
      return engine.deploy(source);
   }
}

