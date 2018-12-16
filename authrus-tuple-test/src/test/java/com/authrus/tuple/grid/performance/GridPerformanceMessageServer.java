package com.authrus.tuple.grid.performance;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.authrus.common.thread.ThreadPool;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.subscribe.SubscriptionAdapter;
import com.authrus.tuple.subscribe.SubscriptionListener;

public class GridPerformanceMessageServer {
   
   private static int ITERATIONS = 100000000;
   private static int MESSAGES = 1000;

   public static void main(String[] list) throws Exception {
      ConsoleAppender console = new ConsoleAppender(); 
      PatternLayout layout = new PatternLayout("%d [%p|%c|%C{1}] %m%n");

      console.setLayout(layout);
      console.setThreshold(Level.INFO);
      console.activateOptions();
      
      Logger.getRootLogger().addAppender(console);
      
      String[] key = new String[] { "Stock" };
      Structure structure = new Structure(key);
      ThreadPool pool = new ThreadPool(10);
      ChangeSubscriber changeSubscriber = new ChangeSubscriber(pool);
      SubscriptionListener logger = new SubscriptionAdapter(); // new SubscriptionLogger();
      SessionRegistry checker = new SessionRegistry();
      SessionRegistryListener tracer = new SessionRegistryListener(checker);
      GridServer server = new GridServer(changeSubscriber, logger, tracer, 32353);
      Grid grid = new Grid(changeSubscriber, structure, "A");

      server.start();
      
      String[] stocks = new String[MESSAGES];
      
      for (int i = 0; i < MESSAGES; i++) {
        stocks[i] = "STOCK-"+i;
      }   
      /**
      AddOrder/0x41 : MsgBase -> # 'A'
      u64 OrderRefNo,
      u8 BuySell,
      u32 Shares,
      string Stock,
      u32 Price         
        */
      for(int i = 0; i < ITERATIONS; i++) {
         Map<String, Object> data = new HashMap<String, Object>();
         Tuple tuple = new Tuple(data, "A");
         int index = i % MESSAGES;
         
         data.put("OrderRefNo", (long)i);
         data.put("BuySell", (char)'B');
         data.put("Shares", (long)i);
         data.put("Stock",  stocks[index]);
         data.put("Price",  i);
         data.put("Time", System.nanoTime());         
         
         grid.update(tuple);
      }
   }
}