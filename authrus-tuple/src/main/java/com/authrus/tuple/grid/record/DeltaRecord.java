package com.authrus.tuple.grid.record;

import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.DeltaMerge;
import com.authrus.tuple.grid.Version;

public class DeltaRecord {
   
   private final DeltaMerge merge;
   private final Version version;
   private final Session session;
   private final String type;
   
   public DeltaRecord(DeltaMerge merge, Version version, Session session, String type) {
      this.version = version;
      this.session = session;
      this.merge = merge;
      this.type = type;
   }
   
   public DeltaMerge getMerge() {
      return merge;
   }   
   
   public Version getVersion() {
      return version;
   }
   
   public Session getSession() {
      return session;
   }
   
   public String getType() {
      return type;
   }
} 
