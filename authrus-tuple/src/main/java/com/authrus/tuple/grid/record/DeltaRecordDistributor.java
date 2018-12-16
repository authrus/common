package com.authrus.tuple.grid.record;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.frame.Session;

public class DeltaRecordDistributor implements DeltaRecordListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(DeltaRecordDistributor.class);
   
   private final List<DeltaRecordListener> listeners;
   
   public DeltaRecordDistributor(List<DeltaRecordListener> listeners) {
      this.listeners = listeners;
   }

   @Override
   public void onUpdate(Session session, DeltaRecord record) {
      for(DeltaRecordListener listener : listeners) {
         try {
            listener.onUpdate(session, record);
         } catch(Exception e) {
            LOG.info("Could not dispatch record from " + session, e);
         }
      }
   }
   
   @Override
   public void onReset(Session session) {
      for(DeltaRecordListener listener : listeners) {
         try {
            listener.onReset(session);
         } catch(Exception e) {
            LOG.info("Could not reset records from " + session, e);
         }
      }
   }
}
