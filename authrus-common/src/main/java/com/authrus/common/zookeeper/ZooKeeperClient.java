package com.authrus.common.zookeeper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import com.fasterxml.jackson.databind.ObjectMapper;

@AllArgsConstructor
public class ZooKeeperClient {

   private final CuratorFramework framework;
   private final ObjectMapper mapper;
   
   @SneakyThrows
   public List<ZooKeeperNode> getNodes(String path) {
      List<String> children = framework.getChildren().forPath(path);
   
      return children.stream()
            .filter(Objects::nonNull)
            .map(name -> getNode(path + "/" + name))
            .collect(Collectors.toList());
   }
   
   @SneakyThrows
   public ZooKeeperNode getNode(String path) {
      int index = path.lastIndexOf("/");
      String name = path.substring(index + 1);
      byte[] data = framework.getData().forPath(path);
      
      if(data != null) {
         String content = new String(data);
         
         return ZooKeeperNode.builder()
               .content(content)
               .path(path)
               .name(name)
               .build();
      }
      return null;
   }

   @SneakyThrows
   public ZooKeeperNode addNode(String path) {
      return addNode(path, Collections.EMPTY_MAP);
   }
   
   @SneakyThrows
   public ZooKeeperNode addNode(String path, Object object) {
      int index = path.lastIndexOf("/");
      String name = path.substring(index + 1);
      String content = mapper.writeValueAsString(object);
      byte[] data = content.getBytes();
      
      framework.create()
         .creatingParentsIfNeeded()
         .withMode(CreateMode.PERSISTENT)
         .forPath(path, data);
      
      return ZooKeeperNode.builder()
            .content(content)
            .path(path)
            .name(name)
            .build();
   }
   
   @SneakyThrows
   public ZooKeeperNode addEphemeralNode(String path) {
      return addEphemeralNode(path, Collections.EMPTY_MAP);
   }
   
   @SneakyThrows
   public ZooKeeperNode addEphemeralNode(String path, Object object) {
      int index = path.lastIndexOf("/");
      String name = path.substring(index + 1);
      String content = mapper.writeValueAsString(object);
      byte[] data = content.getBytes();
      
      framework.create()
         .creatingParentsIfNeeded()
         .withMode(CreateMode.EPHEMERAL)
         .forPath(path, data);
      
      return ZooKeeperNode.builder()
            .content(content)
            .path(path)
            .name(name)
            .build();
   }
   
   @SneakyThrows
   public ZooKeeperNode addEphemeralSequentialNode(String path) {
      return addEphemeralNode(path, Collections.EMPTY_MAP);
   }
   
   @SneakyThrows
   public ZooKeeperNode addEphemeralSequentialNode(String path, Object object) {
      int index = path.lastIndexOf("/");
      String name = path.substring(index + 1);
      String content = mapper.writeValueAsString(object);
      byte[] data = content.getBytes();
      
      framework.create()
         .creatingParentsIfNeeded()
         .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
         .forPath(path, data);
      
      return ZooKeeperNode.builder()
            .content(content)
            .path(path)
            .name(name)
            .build();
   }

   @SneakyThrows
   public ZooKeeperNode deleteNode(String path) {
      int index = path.lastIndexOf("/");
      String name = path.substring(index + 1);
      byte[] data = framework.getData().forPath(path);
      
      if(data != null) {
         String content = new String(data);
         
         framework.delete().forPath(path);
         
         return ZooKeeperNode.builder()
               .content(content)
               .path(path)
               .name(name)
               .build();
      }
      return null;
   }
}
