package com.authrus.tuple.spring.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.DeltaMerge;
import com.authrus.tuple.grid.Row;
import com.authrus.tuple.grid.record.DeltaRecord;

public class DeltaIndex {

   private final Map<String, DeltaRecord> records;
   private final Session session;
   
   public DeltaIndex(Session session) {
      this.records = new ConcurrentHashMap<String, DeltaRecord>();
      this.session = session;
   }
   
   public synchronized List<DeltaRecord> list() {
      List<DeltaRecord> list = new ArrayList<DeltaRecord>();
      
      if(!records.isEmpty()) {
         Set<String> keys = records.keySet();
         
         for(String key : keys) {
            DeltaRecord record = records.get(key);
            
            if(record != null) {
               list.add(record);
            }
         }
      }
      return list;
   }   
   
   public synchronized void update(DeltaRecord record) {
      DeltaMerge merge = record.getMerge();
      String type = record.getType();
      Row row = merge.getCurrent();
      String key = row.getKey();
      
      records.put(session + type + key, record);
   }
   
   public synchronized void clear() {
      records.clear();      
   }
}
