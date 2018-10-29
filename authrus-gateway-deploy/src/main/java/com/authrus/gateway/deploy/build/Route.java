package com.authrus.gateway.deploy.build;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.SneakyThrows;

import com.authrus.gateway.deploy.Context;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.balancer.LoadBalancer;
import com.zuooh.http.proxy.route.MessageAppender;
import com.zuooh.http.proxy.route.RegularExpressionRouter;
import com.zuooh.http.proxy.route.Router;
import com.zuooh.http.proxy.route.append.ClientAddressAppender;
import com.zuooh.http.proxy.route.append.HeaderAppender;
import com.zuooh.http.proxy.route.append.ServerAddressAppender;

@Data
class Route {
   
   private final Map<String, String> headers;
   private final List<String> patterns;
   private final List<RouteRule> rules;
   private final ServerGroup group;
   
   @JsonCreator
   public Route(
         @JsonProperty("route-headers") Map<String, String> headers,
         @JsonProperty("route-patterns") List<String> patterns,
         @JsonProperty("route-rules") List<RouteRule> rules,
         @JsonProperty("server-group") ServerGroup group)
   {
      this.headers = headers;
      this.patterns = patterns;
      this.rules = rules;
      this.group = group;
   }

   @SneakyThrows
   public LoadBalancer createBalancer(Context context, String name){
      Map<String, String> map = new LinkedHashMap<>();
      
      if(patterns == null || patterns.isEmpty()) {
         throw new IllegalStateException("Route must have a at least one match pattern");
      }
      if(rules != null) {
         for(RouteRule rule : rules) {
            String pattern = rule.getPattern();
            String template = rule.getTemplate();
            
            if(pattern == null) {
               throw new IllegalStateException("Rule pattern not defined");
            }
            if(template == null) {
               throw new IllegalStateException("Rule pattern not defined");
            }
            map.put(pattern, template);
         }
      }
      int keepAlive = group.getKeepAlive();
      List<MessageAppender> appenders = new ArrayList<>();
      Router router = new RegularExpressionRouter(map, appenders);
      ServerAddressAppender serverAddress = new ServerAddressAppender(name);
      ClientAddressAppender clientAddress = new ClientAddressAppender();
      MessageAppender keepAliveHeader = new HeaderAppender("Keep-Alive", "timeout=" + Math.abs(keepAlive / 1000));
      
      if(headers != null && !headers.isEmpty()) {
         for(Map.Entry<String, String> entry : headers.entrySet()){
            MessageAppender appender = new HeaderAppender(entry.getKey(), entry.getValue());
            appenders.add(appender);
         }
      }
      appenders.add(keepAliveHeader);
      appenders.add(serverAddress);
      appenders.add(clientAddress);
      
      if(group == null) {
         throw new IllegalStateException("Server group not definied");
      }
      return group.createBalancer(context, router, patterns);
   }
   
}