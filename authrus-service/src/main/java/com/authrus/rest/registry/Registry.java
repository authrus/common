package com.authrus.rest.registry;

import com.authrus.common.zookeeper.ZooKeeperClient;
import com.authrus.common.zookeeper.ZooKeeperNode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@AllArgsConstructor
public class Registry {

   private final ZooKeeperClient client;
   private final ObjectMapper mapper;
   private final String environment;
   private final String host;
   
   @SneakyThrows
   public List<RegistryNode> getNodes() {
      String path = String.format("/%s/%s", environment, host);

      return client.getNodes(path)
         .parallelStream()
         .map(node -> {
            try {
               String content = node.getContent();
               return mapper.readValue(content, RegistryNode.class);
            } catch(Exception e) {
               log.info("Could not read node", e);
            }
            return null;
         })
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
   }
   
   @SneakyThrows
   public RegistryNode getNode(String name) {
      String path = String.format("/%s/%s/%s", environment, host, name);
      ZooKeeperNode node = client.getNode(path);
      
      if(node != null) {
         String content = node.getContent();
         return mapper.readValue(content, RegistryNode.class);
      }
      return null;
   }
   
   @SneakyThrows
   public RegistryNode addNode(String name, RegistryNode node) {
      String path = String.format("/%s/%s/%s", environment, host, name);
      
      try {
         client.deleteNode(path);
      } catch(Exception ignore){ 
      } finally {
         try {
            client.addNode(path, node);
         } catch(Exception e) {
            log.info("Could not register node {}", path, e);
         }
      }
      return node;
   }
   
   @SneakyThrows
   public RegistryNode deleteNode(String name) {
      String path = String.format("/%s/%s/%s", environment, host, name);
      ZooKeeperNode node = client.deleteNode(path);
      
      if(node != null) {
         String content = node.getContent();
         return mapper.readValue(content, RegistryNode.class);
      }
      return null;
   }
}
