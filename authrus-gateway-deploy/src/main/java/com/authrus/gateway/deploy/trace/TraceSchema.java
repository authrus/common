package com.authrus.gateway.deploy.trace;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zuooh.http.proxy.trace.TraceAgent;

public class TraceSchema {

   private final Map<String, TraceSpec> client;
   private final Map<String, TraceSpec> proxy;

   @JsonCreator
   public TraceSchema(
	     @JsonProperty("client") Map<String, TraceSpec> client,
         @JsonProperty("proxy") Map<String, TraceSpec> proxy)
   {
      this.client = client;
      this.proxy = proxy;
   }
   
   public TraceAgent getClientAgent() {
	   
   }
   
   public TraceAgent getProxyAgent() {
	   
   }
}
