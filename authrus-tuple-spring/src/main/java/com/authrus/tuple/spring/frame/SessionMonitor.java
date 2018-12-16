package com.authrus.tuple.spring.frame;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.html.TableDrawer;
import com.authrus.common.time.SampleAverager;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionStatus;

@ManagedResource(description="Monitor status of sessions")
public class SessionMonitor {

   private final SessionRegistry registry;

   public SessionMonitor(SessionRegistry registry) {
      this.registry = registry;
   }

   @ManagedOperation(description="Show active sessions")
   public String showSessions() {
      TableDrawer table = new TableDrawer("session", "connected", "averageSize", "maximumSize", "count");

      for (SessionStatus status : registry) {
         SampleAverager averager = status.getAverager();
         Session session = status.getSession();
         String address = session.toString();         
         boolean connected = session.isOpen();
         long average = averager.average();
         long maximum = averager.maximum();
         long samples = averager.count();

         table.newRow(address, connected, average, maximum, samples);
      }
      return table.drawTable();
   }
   
   @ManagedOperation(description="Close active sessions")
   public void resetSessions() {
      List<Session> sessions = new ArrayList<Session>();
      
      for (SessionStatus status : registry) {
         Session session = status.getSession();
         
         sessions.add(session);
         session.close();         
      }
      for(Session session : sessions) {
         registry.remove(session);
      }
   }
}
