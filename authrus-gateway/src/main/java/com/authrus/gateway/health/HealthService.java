package com.authrus.gateway.health;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import com.authrus.gateway.deploy.EndPoint;
import com.authrus.gateway.deploy.Plan;
import com.google.common.collect.Maps;
import com.zuooh.http.proxy.balancer.status.StatusMonitor;

@Component
@AllArgsConstructor
public class HealthService {
   
   private final Plan plan;
   
   @SneakyThrows
   public Map<String, String> health() {
      Map<String, String> results = Maps.newHashMap();
      Set<EndPoint> servers = plan.getServers();
      
      for(EndPoint entry : servers) {
         StatusMonitor monitor = entry.getMonitor();
         String state = monitor.getLastStatus();
         String address = String.valueOf(entry.getAddress());
         
         results.put(address, state);
      }
      return results;
   }
}
