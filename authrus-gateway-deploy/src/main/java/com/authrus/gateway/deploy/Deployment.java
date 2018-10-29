package com.authrus.gateway.deploy;

import java.io.IOException;
import java.io.Reader;

import lombok.extern.slf4j.Slf4j;

import com.authrus.gateway.deploy.build.SourceInterpolator;
import com.authrus.gateway.deploy.build.SourceProcessor;
import com.authrus.gateway.deploy.build.Specification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuooh.http.proxy.analyser.SampleRecorder;
import com.zuooh.http.proxy.trace.TraceAgent;
import com.zuooh.http.proxy.trace.TraceCollector;

@Slf4j
public class Deployment {
   
   public static final String DEFAULT_COOKIE = "SSOID";
   
   private final Context context;

   public Deployment() throws IOException {
      this((event) -> {});      
   }
   
   public Deployment(TraceCollector collector) throws IOException {
      this(collector, (sample) -> {});
   }
   
   public Deployment(TraceCollector collector, SampleRecorder recorder) throws IOException {
      this(collector, recorder, DEFAULT_COOKIE);
   }
   
   public Deployment(TraceCollector collector, SampleRecorder recorder, String cookie) throws IOException {
      this.context  = new Context(collector, recorder, cookie, 40, 4);
   }

   public Plan deploy(Reader reader) throws Exception {
      ObjectMapper mapper = context.getMapper();
      String text = SourceProcessor.process(reader);     
      String source = SourceInterpolator.interpolate(context, text);
      Specification plan = mapper.readValue(source, Specification.class);
      TraceAgent agent = context.getAgent();
      
      agent.start();
      plan.process(context);
      
      for(FirewallRule rule : context.getRules()) {
         String host = rule.getHost();
         String address = rule.getAddress();
         String type = rule.getType();
         int port = rule.getPort();
         
         log.info("Firewall: open {} port {} on host {}/{}", type, port, host, address);
      }
      return context.compile();
   }
}
