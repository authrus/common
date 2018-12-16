package com.authrus.tuple.grid.replication;

import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.PRIMARY_PORT;
import static com.authrus.tuple.grid.replication.ReplicationMessageConfiguration.SECONDARY_PORT;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.TupleForwarder;
import com.authrus.tuple.TuplePublisher;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.SubscriptionMessageRouter;

public class ReplicationMessageServer {

   public static void main(String[] list) throws Exception {
      ConsoleAppender console = new ConsoleAppender(); 
      PatternLayout layout = new PatternLayout("%d [%p|%c|%C{1}] %m%n");

      console.setLayout(layout);
      console.setThreshold(Level.INFO);
      console.activateOptions();
      
      Logger.getRootLogger().addAppender(console);
      
      createServer("primary", PRIMARY_PORT, SECONDARY_PORT);
      createServer("secondary", SECONDARY_PORT, PRIMARY_PORT);
   }
   
   private static void createServer(String name, int listen, int subscribe) throws Exception {
      Map<String, Grid> grids = new HashMap<String, Grid>();
      String[] key = new String[] { "name" };
      Structure structure = new Structure(key);
      ThreadPool pool = new ThreadPool(10);
      ChangeSubscriber subscriber = new ChangeSubscriber(pool);
      Catalog catalog = new Catalog(grids);
      TuplePublisher publisher = new GridPublisher(catalog);
      SubscriptionMessageRouter router = new SubscriptionMessageRouter(publisher);      
      SessionRegistry checker = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(checker);
      GridServer server = new GridServer(subscriber, router, tracer, listen);
      Grid grid = new Grid(subscriber, structure, "message");

      grids.put("message", grid);
      server.start();
      createSubscriber(name, catalog, subscribe);
   }
   
   private static void createSubscriber(String name, Catalog catalog, int subscribe) throws Exception {
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", subscribe);
      SessionRegistry monitor = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(monitor);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      Subscriber subscriber = new GridSubscriber(tracer, transportBuilder);
      Map<String, String> predicates = new HashMap<String, String>();
      Origin origin = new Origin("test");
      Query query = new Query(origin, predicates);
      TuplePublisher publisher = new GridPublisher(catalog);
      TupleForwarder forwarder = new TupleForwarder(publisher);
      
      predicates.put("message", "*");      
      subscriber.subscribe(forwarder, query);   
   }
}
