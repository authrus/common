package com.authrus.store.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SessionResult {
   
   private String session;
   private int receiveCount;
   private int sendCount;
   private long timeStamp;
   private boolean open;
}
