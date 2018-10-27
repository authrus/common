package com.authrus.rest.manage;

import java.util.Map;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

import lombok.SneakyThrows;

import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuooh.common.manage.jmx.WebConfiguration;
import com.zuooh.common.manage.spring.ApplicationAgent;

@Component
public class ManagementService {

   private final TypeReference<Map<String, String>> reference;
   private final ConnectorServerFactoryBean factory;
   private final WebConfiguration config;
   private final ApplicationAgent agent;
   private final ObjectMapper mapper;
   
   public ManagementService(ConnectorServerFactoryBean factory, ApplicationAgent agent, WebConfiguration config) {
      this.reference = new TypeReference<Map<String, String>>(){};
      this.mapper = new ObjectMapper();
      this.factory = factory;
      this.config = config;
      this.agent = agent;
   }
   
   @SneakyThrows
   public ManagementData getData() {
      int port = config.getPort();
      String address = String.format("http://localhost:%s", port);
      String text = mapper.writeValueAsString(agent);
      Map<String, String> attributes = mapper.readValue(text, reference); 
      JMXConnectorServer server = factory.getObject();
      JMXServiceURL service = server.getAddress();
      String connector = service.toString();
      
      return ManagementData.builder()
            .attributes(attributes)
            .connector(connector)
            .address(address)
            .build();
   }   
}
