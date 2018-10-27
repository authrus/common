package com.authrus.rest.manage;

import com.authrus.rest.registry.RegistryConfiguration;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.ConnectorServerFactoryBean;

import com.zuooh.common.manage.ObjectIntrospector;
import com.zuooh.common.manage.jmx.WebAdministrator;
import com.zuooh.common.manage.jmx.WebConfiguration;
import com.zuooh.common.manage.jmx.WebObjectIntrospector;
import com.zuooh.common.manage.jmx.proxy.ProxyModelExporter;
import com.zuooh.common.manage.spring.ApplicationAgent;
import com.zuooh.common.manage.spring.ApplicationContextIntrospector;
import com.zuooh.common.manage.spring.ApplicationInfo;

@Configuration
@Import(RegistryConfiguration.class)
public class ManagementConfiguration {
   
   private final ConnectorServerFactoryBean factory;
   private final WebConfiguration configuration;
   private final int port;

   public ManagementConfiguration(
         @Value("${jmx.color:#ffffff}") String color,
         @Value("${jmx.login}") String login,
         @Value("${jmx.password}") String password,
         @Value("${jmx.port}") int port)
   {
      this.configuration = new WebConfiguration(color, login, password, port);
      this.factory = new ConnectorServerFactoryBean();
      this.port = port;
   }
   
   @Bean
   @SneakyThrows
   public ApplicationAgent applicationAgent() {
      return new ApplicationInfo();
   }

   @Bean(initMethod = "start")
   public WebAdministrator webAdministrator(WebObjectIntrospector introspector) {
      return new WebAdministrator(configuration, introspector);
   }
   
   @Bean
   public WebConfiguration webConfiguration() {
      return configuration;
   }

   @Bean
   public WebObjectIntrospector webIntrospector(ObjectIntrospector introspector) {
      return new WebObjectIntrospector(introspector);
   }

   @Bean
   public ObjectIntrospector objectIntrospector() {
      return new ApplicationContextIntrospector();
   }

   @Bean
   public ProxyModelExporter modelExporter() {
      return new ProxyModelExporter(true);
   }

   @Bean
   public ConnectorServerFactoryBean connectorServer() {
      factory.setServiceUrl("service:jmx:p2p://localhost:1" + port);
      return factory;
   }
}
