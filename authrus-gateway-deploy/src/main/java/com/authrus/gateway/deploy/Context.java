package com.authrus.gateway.deploy;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import lombok.Data;

import org.simpleframework.common.thread.ConcurrentExecutor;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuooh.http.proxy.analyser.SampleRecorder;
import com.zuooh.http.proxy.balancer.identity.CookieExtractor;
import com.zuooh.http.proxy.balancer.identity.IdentityExtractor;
import com.zuooh.http.proxy.core.exchange.RequestExchanger;
import com.zuooh.http.proxy.trace.TraceAgent;
import com.zuooh.http.proxy.trace.TraceCollector;

@Data
public class Context {
   
   private final Set<FirewallRule> rules;
   private final Set<EndPoint> monitors;
   private final Set<URI> addresses;  
   private final SampleRecorder recorder;
   private final IdentityExtractor extractor;
   private final ObjectMapper mapper;
   private final TraceAgent agent; 
   private final Executor executor;
   private final Reactor reactor; 

   public Context(TraceCollector collector, SampleRecorder recorder, String cookie) throws IOException {
      this(collector, recorder, cookie, 40);
   }
   
   public Context(TraceCollector collector, SampleRecorder recorder, String cookie, int threads) throws IOException {
      this(collector, recorder, cookie, threads, 4);
   }
   
   public Context(TraceCollector collector, SampleRecorder recorder, String cookie, int threads, int selectors) throws IOException {
      this.executor = new ConcurrentExecutor(RequestExchanger.class, threads);
      this.reactor = new ExecutorReactor(executor, selectors);
      this.extractor = new CookieExtractor(cookie);
      this.agent = new TraceAgent(collector);
      this.monitors = new CopyOnWriteArraySet<>();
      this.rules = new CopyOnWriteArraySet<>();
      this.addresses = new CopyOnWriteArraySet<>();
      this.mapper = new ObjectMapper();
      this.recorder = recorder;
   }
   
   public Plan compile() {      
      return new Plan(rules, monitors, addresses);
   }
}