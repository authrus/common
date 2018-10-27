package com.authrus.terminal.process;

import com.authrus.rest.registry.RegistryNode;
import com.authrus.terminal.process.Process;

public enum OperatingSystem {
   LINUX {
      @Override
      public Process create(RegistryNode node) {
         return UnixProcess.builder()
               .node(node)
               .build();
      }
   },
   WINDOWS {
      @Override
      public Process create(RegistryNode node) {
         return WindowsProcess.builder()
               .node(node)
               .build();
      }
   },
   MAC {
      @Override
      public Process create(RegistryNode node) {
         return UnixProcess.builder()
               .node(node)
               .build();
      }
   };
   
   
   public abstract Process create(RegistryNode node);
   
   public static OperatingSystem resolveSystem() {
      OperatingSystem[] values = OperatingSystem.values();
      String system = System.getProperty("os.name");
      String token = system.toLowerCase();
      
      for(OperatingSystem os : values) {
         if(token.startsWith(os.name().toLowerCase())) {
            return os;
         }
      }
      return LINUX;
   }
}
