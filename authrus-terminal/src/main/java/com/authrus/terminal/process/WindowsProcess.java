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
public class WindowsProcess implements Process {

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
      
      return new Script("java -jar target/" + name + ".jar", file);
   }
   
   @Override
   public Script stop() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("echo not supported", file);
   }
   
   @Override
   public Script tail() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("tail log/app.log", file);
   }
   
   @Override
   public Script image() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      File file = path.toFile();
      
      return new Script("echo not supported", file);
   }
}
