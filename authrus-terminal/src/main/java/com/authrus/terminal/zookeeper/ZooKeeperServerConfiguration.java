package com.authrus.terminal.zookeeper;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

@Slf4j
@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = ZooKeeperServerConfiguration.class)
public class ZooKeeperServerConfiguration {

   public static final String PROPERTY_PREFIX = "zookeeper.";
   public static final String THREAD_NAME = "zookeeper";
   
   private final Environment environment;

   @PostConstruct
   public void start() {
      Properties props = new Properties();
      QuorumPeerConfig config = new QuorumPeerConfig();
      ServerConfig serverConfig = new ServerConfig();
      ZooKeeperServerMain server = new ZooKeeperServerMain();
      Thread thread = new Thread(() -> {
         try {
            MutablePropertySources sources = ((AbstractEnvironment) environment).getPropertySources();
            StreamSupport.stream(sources.spliterator(), false)
                    .filter(source -> source instanceof EnumerablePropertySource)
                    .map(source -> ((EnumerablePropertySource<?>) source).getPropertyNames())
                    .flatMap(Arrays::<String>stream)
                    .filter(property -> property.startsWith(PROPERTY_PREFIX))
                    .forEach(property -> {
                       String name = property.replace(PROPERTY_PREFIX, "");
                       String value = environment.getProperty(property);
                             
                       props.setProperty(name, value);
                    });
            config.parseProperties(props);
            serverConfig.readFrom(config);
            server.runFromConfig(serverConfig);
         } catch(Exception e) {
            log.info("Could not log properties", e);
         }
      });
      thread.setName(THREAD_NAME);
      thread.start();
   }
}
