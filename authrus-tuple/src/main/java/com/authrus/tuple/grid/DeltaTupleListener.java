package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;

public class DeltaTupleListener implements DeltaMergeListener {

   private static final Logger LOG = LoggerFactory.getLogger(DeltaTupleListener.class);
   
   private final TupleListener listener;
   private final long expiry;
   
   public DeltaTupleListener(TupleListener listener) {
      this(listener, 30000);
   }
   
   public DeltaTupleListener(TupleListener listener, long expiry) {      
      this.listener = listener;
      this.expiry = expiry;
   }
   
   @Override
   public void onMerge(DeltaMerge merge, String type) {
      Row row = merge.getCurrent();
      Cell[] cells = row.getCells();
      Key key = merge.getKey();

      if (cells != null && cells.length > 0) {
         Map<String, Object> data = new HashMap<String, Object>(cells.length * 2);

         for (Cell cell : cells) {
            if (cell != null) {
               String name = cell.getColumn();
               Object value = cell.getValue();

               if (value != null) {
                  data.put(name, value);
               }
            }
         }
         long time = System.currentTimeMillis();
         long change = row.getChange();
         long update = row.getTime();
         
         if(update + expiry > time) {
            Tuple tuple = new Tuple(data, type, change);
               
            try {
               if(listener != null) {
                  listener.onUpdate(tuple);
               }
            } catch (Throwable e) {
               LOG.info("Error dispatching message " + tuple + " of type '" + type + "' at  " + key, e);
            }
         }         
      }
   }
}
