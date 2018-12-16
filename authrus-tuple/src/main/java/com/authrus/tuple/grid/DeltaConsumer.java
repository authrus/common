package com.authrus.tuple.grid;

import java.io.IOException;
import java.util.List;

import com.authrus.io.DataConsumer;
import com.authrus.io.DataFormatter;
import com.authrus.io.DataReader;

public class DeltaConsumer implements DataConsumer {

   private final DeltaMergeListener listener;
   private final DataFormatter formatter;
   private final DeltaMerger merger;
   private final DeltaReader reader;
   private final String type;

   public DeltaConsumer(DeltaMergeListener listener, String type) {
      this.formatter = new DataFormatter();
      this.reader = new DeltaReader(formatter);
      this.merger = new DeltaMerger();
      this.listener = listener;
      this.type = type;
   }

   @Override
   public synchronized void consume(DataReader input) throws Exception {
      Delta delta = reader.readDelta(input, type);
      TableDelta table = delta.getTable();
      String type = delta.getType();

      try {
         consume(table, type);
      } catch (Exception e) {
         throw new IOException("Could not process delta " + delta, e);
      } finally {
         formatter.reset();
      }
      
   }

   private synchronized void consume(TableDelta table, String type) throws Exception {
      List<DeltaMerge> merges = merger.mergeTableDelta(table);

      for (DeltaMerge merge : merges) {
         try {
            listener.onMerge(merge, type);
         } catch(Throwable e) {
            throw new IOException("Could not process merge " + merge, e);
         }
         
      }
   }
}
