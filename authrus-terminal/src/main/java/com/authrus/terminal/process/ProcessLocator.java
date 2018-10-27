package com.authrus.terminal.process;

import com.authrus.rest.registry.RegistryNode;
import com.authrus.rest.registry.RegistryService;
import lombok.AllArgsConstructor;

import com.zuooh.common.command.Environment;

@AllArgsConstructor
public class ProcessLocator {

   private RegistryService service;
   private Environment environment;
   private TailParser parser;
   
   public ProcessManager locate(String name) {
      OperatingSystem system = OperatingSystem.resolveSystem();
      RegistryNode node = service.getNode(name);
      
     if(node != null) { 
        Process process = system.create(node);
        
        return ProcessManager.builder()
            .process(process)
            .environment(environment)
            .parser(parser)
            .build();
     }
     return null;
   }
}
