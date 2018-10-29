package com.authrus.gateway.deploy.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

import com.authrus.gateway.deploy.Context;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.balancer.LoadBalancer;
import com.zuooh.http.proxy.balancer.MasterSlaveSelector;
import com.zuooh.http.proxy.balancer.SelectorLoadBalancer;
import com.zuooh.http.proxy.balancer.ServerActivitySelector;
import com.zuooh.http.proxy.balancer.ServerConnector;
import com.zuooh.http.proxy.balancer.ServerSelector;
import com.zuooh.http.proxy.balancer.identity.IdentityExtractor;
import com.zuooh.http.proxy.route.Router;

@Data
@ToString 
class ServerGroup {
   
   private final HealthCheck check;
   private final List<String> servers;
   private final String selector;
   private final int keepAlive;
   private final int timeout;
   private final int buffer; 
   
   @JsonCreator
   public ServerGroup(
         @JsonProperty("health-check") HealthCheck check,
         @JsonProperty("servers") List<String> servers,
         @JsonProperty("selector") String selector,
         @JsonProperty("keep-alive") int keeyAlive,
         @JsonProperty("timeout") int timeout,
         @JsonProperty("buffer") int buffer)
   {
      this.keepAlive = Math.max(keeyAlive, 20000);
      this.timeout = Math.max(timeout, 10000);
      this.buffer = Math.max(buffer, 8192);
      this.servers = servers;
      this.selector = selector;
      this.check = check;
   }     
   
   public int getKeepAlive() {
      return (keepAlive <= 0 ? 20000 : keepAlive) + 10000;
   }
   
   public LoadBalancer createBalancer(Context context, Router router, List<String> patterns) {
      ServerSelector selector = createSelector(context, patterns);
      IdentityExtractor extractor = context.getExtractor();
      
      return new SelectorLoadBalancer(selector, extractor, router);
   }

   public ServerSelector createSelector(Context context, List<String> patterns) {
      List<ServerConnector> connectors = new ArrayList<ServerConnector>();
      
      if(check == null) {
         throw new IllegalStateException("Health check details required");
      }
      check.validate(servers);
      
      String path = check.getPath();
      Map<String, String> headers = check.getHeaders();
      long frequency = check.getFrequency();
      
      for(String server : servers) {
         if(server == null) {
            throw new IllegalStateException("Illegal null address in " + servers);
         }
         ServerAddress address = new ServerAddress(server, path);
         ServerDefinition uri = new ServerDefinition(headers, address, keepAlive + 10000, timeout, frequency, buffer);
         ServerConnector connector = uri.createConnector(context, patterns);
         connectors.add(connector);
      }
      SelectorType type = SelectorType.resolve(selector);
      
      if(type == SelectorType.DYNAMIC) {
         return new ServerActivitySelector(connectors, (identity) -> null);
      }
      return new MasterSlaveSelector(connectors);
   }
   
}