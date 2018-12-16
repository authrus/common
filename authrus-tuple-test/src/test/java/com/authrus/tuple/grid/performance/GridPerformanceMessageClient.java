package com.authrus.tuple.grid.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.SocketBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;

public class GridPerformanceMessageClient {

   public static void main(String[] list) throws Exception {
      ConsoleAppender console = new ConsoleAppender(); 
      PatternLayout layout = new PatternLayout("%d [%p|%c|%C{1}] %m%n");

      console.setLayout(layout);
      console.setThreshold(Level.INFO);
      console.activateOptions();
      
      Logger.getRootLogger().addAppender(console);      
      
      AtomicInteger successes = new AtomicInteger();
      AtomicInteger failures = new AtomicInteger();
      GridPerformanceUpdateListener listener = new GridPerformanceUpdateListener("client", successes, failures);
      TraceAgent analyzer = new TraceAgent();
      SocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", 32353);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(monitor);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      Map<String, String> predicates = new HashMap<String, String>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);

      predicates.put("A", "*");
      listener.start();
      subscriber.subscribe(listener, query);
   }
}