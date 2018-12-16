package com.authrus.tuple.spring.subscribe;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.html.TableDrawer;
import com.authrus.common.html.TableRowDrawer;
import com.authrus.common.time.DateTime;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.subscribe.SubscriptionListener;

@ManagedResource(description="Monitor subscription activity")
public class SubscriptionMonitor implements SubscriptionListener {

   private final Cache<String, SubscriptionStatus> history;

   public SubscriptionMonitor() {
      this(1000);
   }

   public SubscriptionMonitor(int capacity) {
      this.history = new LeastRecentlyUsedCache<String, SubscriptionStatus>(capacity);
   }

   @ManagedOperation(description="Show subscription details")
   public String showSubscriptions() {
      TableDrawer printer = new TableDrawer("address", "creationTime", "lastUpdate", "messageCount", "query", "error");

      if (!history.isEmpty()) {
         Set<String> addresses = history.keySet();

         for (String address : addresses) {
            TableRowDrawer tableRow = printer.newRow();
            SubscriptionStatus status = history.fetch(address);
            Exception error = status.getError();
            DateTime creationTime = status.getCreationTime();
            DateTime updateTime = status.getUpdateTime();     
            Query query = status.getQuery();
            long count = status.getUpdateCount();       

            if (status.isActive()) {
               tableRow.setColor("#00ff00");
            } else {
               tableRow.setColor("#ff0000");
            }
            tableRow.setNormal("address", address);
            tableRow.setNormal("creationTime", creationTime);
            tableRow.setNormal("lastUpdate", updateTime);
            tableRow.setNormal("messageCount", count);
            tableRow.setNormal("query", query);
            tableRow.setCode("error", error);
         }
      }
      return printer.drawTable();
   }

   @Override
   public void onException(String address, Exception cause) {
      SubscriptionStatus status = history.fetch(address);

      if (status != null) {
         status.setError(cause);
         status.setActive(false);
      }
   }

   @Override
   public void onSubscribe(String address, Query query) {
      SubscriptionStatus status = new SubscriptionStatus(address);

      if (address != null) {         
         history.cache(address, status);
         status.setQuery(query);
      }
   }

   @Override
   public void onConnect(String address) {
      SubscriptionStatus status = history.take(address);

      if (status != null) {
         status.setActive(false);
      }
   }

   @Override
   public void onClose(String address) {
      SubscriptionStatus status = history.fetch(address);

      if (status != null) {
         status.setActive(false);
      }
   }

   @Override
   public void onHeartbeat(String address) {
      SubscriptionStatus status = history.fetch(address);
      long time = System.currentTimeMillis();

      if (status != null) {
         status.setUpdateTime(time);
      }
   }   

   @Override
   public void onUpdate(String address, Tuple tuple) {
      SubscriptionStatus status = history.fetch(address);      
      long time = System.currentTimeMillis();
      
      if(status != null) {  
         status.setUpdateTime(time);
      }     
   }

   private static class SubscriptionStatus {

      private final AtomicReference<Long> update;
      private final AtomicReference<Exception> error;
      private final AtomicReference<Query> query;
      private final AtomicBoolean active;
      private final AtomicLong count;
      private final String address;
      private final DateTime time;

      public SubscriptionStatus(String address) {
         this.update = new AtomicReference<Long>();
         this.error = new AtomicReference<Exception>();
         this.query = new AtomicReference<Query>();
         this.active = new AtomicBoolean(true);
         this.count = new AtomicLong();
         this.time = DateTime.now();
         this.address = address;
      }

      public DateTime getCreationTime() {
         return time;
      }

      public boolean isActive() {
         return active.get();
      }

      public void setActive(boolean flag) {
         active.set(flag);
      }

      public Query getQuery() {
         return query.get();
      }

      public void setQuery(Query value) {
         query.set(value);
      }

      public Exception getError() {
         return error.get();
      }

      public void setError(Exception value) {
         error.set(value);
      }

      public long getUpdateCount() {
         return count.get();
      }

      public DateTime getUpdateTime() {
         Long time = update.get();
         
         if(time != null) {
            return DateTime.at(time);
         }
         return null;
      }

      public void setUpdateTime(Long time) {
         update.set(time);
         count.getAndIncrement();
      }

      @Override
      public String toString() {
         return address;
      }
   }
}
