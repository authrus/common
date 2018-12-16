package com.authrus.tuple.grid.record;

import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.DeltaMerge;
import com.authrus.tuple.grid.DeltaMergeListener;
import com.authrus.tuple.grid.Row;
import com.authrus.tuple.grid.Version;

public class DeltaRecordBuilder implements DeltaMergeListener {
   
   private final DeltaRecordListener listener;
   private final Version version;
   private final Session session;   
   
   public DeltaRecordBuilder(DeltaRecordListener listener, Session session) {
      this.version = new Version(-1);
      this.listener = listener;
      this.session = session;
   }

   @Override
   public void onMerge(DeltaMerge merge, String type) {
      Version next = version.update();
      Row row = merge.getCurrent();
      
      if(row != null) {
         DeltaRecord record = new DeltaRecord(merge, next, session, type);
         
         if(listener != null) {
            listener.onUpdate(session, record);
         }
      }
   } 
}
