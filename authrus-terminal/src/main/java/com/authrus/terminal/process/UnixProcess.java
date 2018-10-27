package com.authrus.terminal.process;

import com.authrus.rest.registry.RegistryNode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AllArgsConstructor;
import lombok.Builder;

import com.zuooh.common.command.Script;

@Builder
@AllArgsConstructor
public class UnixProcess implements Process {
   
   private final RegistryNode node;
   
   @Override
   public String getName() {
      return node.getName();
   }

   @Override
   public String getHost() {
      return node.getHost();
   }
   
   @Override
   public File getDirectory() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      
      return path.toFile();
   }
   
   @Override
   public Script start() {
      String name = node.getName();
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("/usr/bin/sudo -u root /sbin/service " + name + " start", file);
   }
   
   @Override
   public Script stop() {
      String name = node.getName();
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("/usr/bin/sudo -u root /sbin/service " + name + " stop", file);
   }
   
   @Override
   public Script tail() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("/usr/bin/tail -n10 app.log app.stderr app.stdout", file);
   }
   
   @Override
   public Script image() {
      String name = node.getName();
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("/bin/ps -eaf | grep " + name + ".jar | sed /grep/d", file);
   }
}
