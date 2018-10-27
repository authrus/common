package com.authrus.terminal.service;

import com.authrus.rest.container.ServiceRegistry;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;

import org.simpleframework.http.socket.service.Service;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TerminalConfiguration {
        
   private final Optional<List<Service>> services;
   private final ServiceRegistry registry;
   
   @PostConstruct
   public void register(){
      if(services.isPresent()) {
         List<Service> list = services.get();
         
         for(Service service : list) {
            registry.register("/session", service);
         }
      }
   }
}
