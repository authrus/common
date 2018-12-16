package com.authrus.tuple.spring.grid;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.record.DeltaRecord;
import com.authrus.tuple.grid.record.DeltaRecordListener;

public class DeltaIndexer implements DeltaRecordListener {
   
   private final Cache<Session, DeltaIndex> indexes;
   private final DeltaSearcher searcher;
   
   public DeltaIndexer(DeltaSearcher searcher) {
      this.indexes = new LeastRecentlyUsedCache<Session, DeltaIndex>();
      this.searcher = searcher;
   }

   @Override
   public void onUpdate(Session session, DeltaRecord record) {
      DeltaIndex index = indexes.fetch(session);
            
      if(index == null) {
         index = new DeltaIndex(session);
         indexes.cache(session, index);
      }
      index.update(record);
      searcher.update(session, index);      
   }

   @Override
   public void onReset(Session session) {
      DeltaIndex index = indexes.take(session);
      
      if(index != null) {
         index.clear();
      }
   }
}
