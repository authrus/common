package com.authrus.store.resource;

import java.util.List;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuooh.tuple.frame.Session;
import com.zuooh.tuple.frame.SessionRegistry;
import com.zuooh.tuple.frame.SessionStatus;

@Component
@AllArgsConstructor
public class SessionService {

   private final SessionRegistry registry;
   
   public List<SessionResult> getSessions() {
      List<SessionResult> list = Lists.newArrayList();
      
      for(SessionStatus status : registry) {
         Session session = status.getSession();
         String address = session.toString();
         int sendCount = status.getSendCount().get();
         int receiveCount = status.getReceiveCount().get();
         long timeStamp = status.getTimeStamp().get();
         boolean open = session.isOpen();
         SessionResult result = SessionResult.builder()
               .session(address)
               .sendCount(sendCount)
               .receiveCount(receiveCount)
               .timeStamp(timeStamp)
               .open(open)
               .build();
         
         list.add(result);
      }
      return list;
   }
}
