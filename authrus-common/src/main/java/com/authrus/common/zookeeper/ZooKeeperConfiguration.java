package com.authrus.common.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = ZooKeeperConfiguration.class)
public class ZooKeeperConfiguration {

   private final ObjectMapper mapper;
   private final RetryPolicy policy;
   private final String hosts;
   private final boolean enabled;
   
   public ZooKeeperConfiguration(
         @Value("${zookeeper.hosts}") String hosts,
         @Value("${zookeeper.retries:100}") int retries,
         @Value("${zookeeper.enabled:true}") boolean enabled) 
   {
      this.policy = new RetryNTimes(retries, 5000);
      this.mapper = new ObjectMapper();
      this.enabled = enabled;
      this.hosts = hosts;
   }
   
   @Bean
   @SneakyThrows
   public ZooKeeperClient zooKeeperClient() {
      CuratorFramework framework = CuratorFrameworkFactory.newClient(hosts, policy);
      
      if(enabled) {
         log.info("Connecting to {}", hosts);
         framework.start();
      }
      return new ZooKeeperClient(framework, mapper);
   }
}