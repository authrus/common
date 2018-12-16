package com.authrus.tuple.frame;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.time.SampleAverager;

public class SessionRegistry implements Iterable<SessionStatus> {

   private final Cache<Session, SessionStatus> sessions;
   private final Set<SessionStatus> statistics;

   public SessionRegistry() {
      this(2000);
   }
   
   public SessionRegistry(int capacity) {
      this.sessions = new LeastRecentlyUsedCache<Session, SessionStatus>(capacity);
      this.statistics = new CopyOnWriteArraySet<SessionStatus>();
   }

   public Iterator<SessionStatus> iterator() {
      return Collections.unmodifiableSet(statistics).iterator();
   }

   public void register(Session session) {
      SessionStatus status = new SessionStatus(session);
      
      if(!sessions.contains(session)) {
         AtomicLong timeStamp = status.getTimeStamp();
         long currentTime = System.currentTimeMillis();
         
         timeStamp.set(currentTime);
         sessions.cache(session, status);
         statistics.add(status);
      }
   }
   
   public void receipt(Session session) {
      SessionStatus status = sessions.fetch(session);
      
      if(status != null) {
         AtomicInteger receiveCount = status.getReceiveCount();
         AtomicLong timeStamp = status.getTimeStamp();
         long currentTime = System.currentTimeMillis();
         
         timeStamp.set(currentTime);
         receiveCount.getAndIncrement();
      }
   }
   
   public void update(Session session, int size) {
      SessionStatus status = sessions.fetch(session);
      
      if(status != null) {
         AtomicInteger sendCount = status.getSendCount();
         SampleAverager averager = status.getAverager();
         AtomicLong timeStamp = status.getTimeStamp();
         long currentTime = System.currentTimeMillis();
         
         timeStamp.set(currentTime);
         sendCount.getAndIncrement();
         averager.sample(size);
      }
   }
   
   public void remove(Session session) {
      SessionStatus status = sessions.take(session);
      
      if(status != null) {
         statistics.remove(status);
      }
   }
   
   public boolean contains(Session session) {
      return sessions.contains(session);
   }
}
