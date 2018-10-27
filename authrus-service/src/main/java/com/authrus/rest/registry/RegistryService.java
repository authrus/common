package com.authrus.rest.registry;

import java.util.List;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegistryService {

   private final Registry registry;
   
   public List<RegistryNode> getNodes() {
      return registry.getNodes();
   }
   
   public RegistryNode getNode(String name) {
      return registry.getNode(name);
   }
}
