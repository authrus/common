package com.authrus.gateway.deploy;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import com.authrus.gateway.deploy.build.Context;
import com.authrus.gateway.deploy.build.FirewallRule;
import com.authrus.gateway.deploy.build.Specification;
import com.authrus.gateway.deploy.parse.SourceInterpolator;
import com.authrus.gateway.deploy.parse.SourceProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuooh.http.proxy.analyser.SampleRecorder;
import com.zuooh.http.proxy.trace.TraceAgent;
import com.zuooh.http.proxy.trace.TraceCollector;

@Slf4j
public class DeploymentEngine {
   
   public static final String DEFAULT_COOKIE = "SSOID";
   
   private final Context context;

   public DeploymentEngine() throws IOException {
      this((event) -> {});      
   }
   
   public DeploymentEngine(TraceCollector collector) throws IOException {
      this(collector, (sample) -> {});
   }
   
   public DeploymentEngine(TraceCollector collector, SampleRecorder recorder) throws IOException {
      this(collector, recorder, DEFAULT_COOKIE);
   }
   
   public DeploymentEngine(TraceCollector collector, SampleRecorder recorder, String cookie) throws IOException {
      this.context  = new Context(collector, recorder, cookie, 40, 4);
   }

   public Deployment deploy(Reader reader) throws Exception {
      ObjectMapper mapper = context.getMapper();
      String text = SourceProcessor.process(reader);     
      String source = SourceInterpolator.interpolate(context, text);
      Specification plan = mapper.readValue(source, Specification.class);
      TraceAgent agent = context.getAgent();
      
      agent.start();
      plan.process(context);
      
      Set<FirewallRule> rules = context.getRules();
      
      for(FirewallRule rule : rules) {
         String host = rule.getHost();
         String address = rule.getAddress();
         String type = rule.getType();
         int port = rule.getPort();
         
         log.info("Firewall: open {} port {} on host {}/{}", type, port, host, address);
      }
      return context.getPlan();
   }
}
