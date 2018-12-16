package com.authrus.tuple.frame;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.authrus.common.time.SampleAverager;

public class SessionStatus {

   private final AtomicInteger receiveCount;
   private final AtomicInteger sendCount; 
   private final SampleAverager averager;
   private final AtomicInteger sequence;  
   private final AtomicLong timeStamp;
   private final Session session;
   
   public SessionStatus(Session session) {      
      this.receiveCount = new AtomicInteger();
      this.sendCount = new AtomicInteger();
      this.sequence = new AtomicInteger();
      this.averager = new SampleAverager();
      this.timeStamp = new AtomicLong();
      this.session = session;
   }
   
   public AtomicInteger getReceiveCount() {
      return receiveCount;
   }
   
   public AtomicInteger getSendCount() {
      return sendCount;
   }
   
   public AtomicInteger getSequence() {
      return sequence;
   }
   
   public AtomicLong getTimeStamp(){
      return timeStamp;
   }
   
   public SampleAverager getAverager() {
      return averager;
   }
   
   public Session getSession() {
      return session;
   }
}
