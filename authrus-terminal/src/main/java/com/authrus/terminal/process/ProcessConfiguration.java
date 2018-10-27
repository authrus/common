package com.authrus.terminal.process;

import com.authrus.rest.registry.RegistryConfiguration;
import com.authrus.rest.registry.RegistryService;

import java.util.Collections;

import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.zuooh.common.command.Environment;
import com.zuooh.common.command.MapEnvironment;

@Configuration
@Import(RegistryConfiguration.class)
public class ProcessConfiguration {
   
   private final RegistryService service;
   private final Environment environment;
   private final TailParser parser;
   
   public ProcessConfiguration(RegistryService service) {
      this.environment = new MapEnvironment(Collections.EMPTY_MAP);
      this.parser = new TailParser();
      this.service = service;
   }

   @Bean
   @SneakyThrows
   public ProcessLocator processLocator() {
      return new ProcessLocator(service, environment, parser);
   }
}
