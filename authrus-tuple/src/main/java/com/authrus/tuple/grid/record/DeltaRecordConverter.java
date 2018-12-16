package com.authrus.tuple.grid.record;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.Session;
import com.authrus.tuple.grid.Cell;
import com.authrus.tuple.grid.DeltaMerge;
import com.authrus.tuple.grid.DeltaTupleListener;
import com.authrus.tuple.grid.Row;

public class DeltaRecordConverter implements DeltaRecordListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(DeltaTupleListener.class);
   
   private final TupleListener listener;
   
   public DeltaRecordConverter(TupleListener listener) {
      this.listener = listener;
   }
   
   @Override
   public void onUpdate(Session session, DeltaRecord record) {
      DeltaMerge merge = record.getMerge();
      String type = record.getType();
      Row row = merge.getCurrent();
      Cell[] cells = row.getCells();

      if (cells != null && cells.length > 0) {
         Map<String, Object> data = new HashMap<String, Object>();

         for (Cell cell : cells) {
            if (cell != null) {
               String name = cell.getColumn();
               Object value = cell.getValue();

               if (value != null) {
                  data.put(name, value);
               }
            }
         }
         Tuple tuple = new Tuple(data, type);       

         try {
            if(listener != null) {
               listener.onUpdate(tuple);
            }
         } catch (Throwable e) {
            LOG.info("Error dispatching message " + tuple + " of " + type + " from " + session, e);
         }         
      }
   }
   
   @Override
   public void onReset(Session session) {
      try {
         if(listener != null) {
            listener.onReset();
         }
      } catch (Throwable e) {
         LOG.info("Error resetting messages from " + session, e);
      }
   }
}
