package com.authrus.rest.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;

import org.simpleframework.http.socket.service.Service;

public class ServiceRegistry {

   protected final Map<String, Service> services;

   @SneakyThrows
   public ServiceRegistry() {
      this.services = new ConcurrentHashMap<String, Service>();
   }
   
   public void register(String path, Service service) {
      services.put(path, service);
   }
   
   public void remove(String path) {
      services.remove(path);
   }
}
