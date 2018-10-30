package com.authrus.gateway.deploy.build;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.TraceAnalyzer;

import com.zuooh.common.ssl.DefaultCertificate;
import com.zuooh.http.proxy.alarm.Alarm;
import com.zuooh.http.proxy.alarm.LogAlarm;
import com.zuooh.http.proxy.analyser.SampleRecorder;
import com.zuooh.http.proxy.balancer.ServerConnector;
import com.zuooh.http.proxy.balancer.connect.ConnectionBuilder;
import com.zuooh.http.proxy.balancer.connect.ConnectionManager;
import com.zuooh.http.proxy.balancer.connect.ConnectionPool;
import com.zuooh.http.proxy.balancer.connect.ConnectionPoolConnector;
import com.zuooh.http.proxy.balancer.connect.ConnectionSampler;
import com.zuooh.http.proxy.balancer.connect.DirectConnectionBuilder;
import com.zuooh.http.proxy.balancer.connect.SecureConnectionBuilder;
import com.zuooh.http.proxy.balancer.status.ConnectChecker;
import com.zuooh.http.proxy.balancer.status.PingChecker;
import com.zuooh.http.proxy.balancer.status.ServerRequestBuilder;
import com.zuooh.http.proxy.balancer.status.StatusChecker;
import com.zuooh.http.proxy.balancer.status.StatusMonitor;
import com.zuooh.http.proxy.route.MessageAppender;
import com.zuooh.http.proxy.route.MessageRouter;
import com.zuooh.http.proxy.route.Router;
import com.zuooh.http.proxy.route.append.HeaderAppender;

@Slf4j
@AllArgsConstructor 
class ServerDefinition {
   
   private Map<String, String> headers;
   private ServerAddress address;
   private int keepAlive;
   private int timeout;
   private long frequency;
   private int buffer;
   
   @SneakyThrows
   public ServerConnector createConnector(Context context, List<String> patterns) {
      String path = address.getPath();
      URI uri = address.getURI();
      String ip = address.getIPAddress();
      String hostHeader = address.getHostHeader();
      SampleRecorder recorder = context.getRecorder();
      int port = address.getPort();
      
      log.info("Creating a connector for {}", address);
      
      ConnectionSampler sampler = new ConnectionSampler(recorder);
      ConnectionPool pool = createPool(context, sampler);
      ConnectionPoolConnector connector = new ConnectionPoolConnector(pool, sampler);
      StatusChecker checker = null;
      
      if(path != null) {
         checker = createPingChecker(context, path, hostHeader, connector);
      } else {
         checker = new ConnectChecker(connector);
      }
      Alarm alarm = new LogAlarm();
      StatusMonitor monitor = new StatusMonitor(pool, checker, alarm, frequency);
      EndPoint endPoint = new EndPoint(connector, monitor, patterns, uri);
      FirewallRule rule = new FirewallRule("TCP", hostHeader, ip, port);
      
      context.getRules().add(rule);
      context.getMonitors().add(endPoint);
      monitor.start();
      
      return connector;
   }

   private StatusChecker createPingChecker(Context context, String path, String hostHeader, ConnectionPoolConnector connector) {
      StatusChecker checker;
      Reactor reactor = context.getReactor();
      List<MessageAppender> appenders = new ArrayList<MessageAppender>();
      Router router = new MessageRouter(appenders);
      
      if(headers != null && !headers.isEmpty()) {
         for(Map.Entry<String, String> entry : headers.entrySet()){
            MessageAppender appender = new HeaderAppender(entry.getKey(), entry.getValue());
            appenders.add(appender);
         }
      }
      MessageAppender appender = new HeaderAppender("Keep-Alive", "timeout=" + keepAlive / 1000);
      appenders.add(appender);
      ServerRequestBuilder builder = new ServerRequestBuilder(connector, reactor, router);
      checker = new PingChecker(builder, hostHeader, path, "", timeout);
      return checker;
   }

   @SneakyThrows
   public ConnectionPool createPool(Context context, ConnectionSampler sampler) {
      ConnectionBuilder builder = createBuilder(context, buffer);
      ConnectionManager manager = new ConnectionManager(builder, sampler, keepAlive);
      manager.start();
      return new ConnectionPool(manager);
   }
   
   @SneakyThrows
   public ConnectionBuilder createBuilder(Context context, int buffer) {
      TraceAnalyzer analyzer = context.getAgent();
      Reactor reactor = context.getReactor();
      String host = address.getHost();
      int port = address.getPort();
      
      if(address.isSecure()) {
         DefaultCertificate certificate = new DefaultCertificate();
         return new SecureConnectionBuilder(analyzer, certificate, reactor, host, port, buffer);
      }
      return new DirectConnectionBuilder(analyzer, reactor, host, port, buffer);
   }
}