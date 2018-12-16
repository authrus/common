package com.authrus.tuple.frame;

import com.authrus.io.DataFormatter;
import com.authrus.io.DataWriter;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.query.QueryWriter;
import com.authrus.tuple.queue.ElementBatch;
import com.authrus.tuple.queue.ElementWriter;

class SessionWriter {

   private final ElementWriter elementWriter;
   private final QueryWriter queryWriter;
   
   public SessionWriter(DataFormatter formatter) { 
      this.queryWriter = new QueryWriter();      
      this.elementWriter = new ElementWriter(formatter);       
   }   
   
   public void writeQuery(DataWriter writer, Query query) throws Exception {
      queryWriter.writeQuery(writer, query);
   }
   
   public void writeBatch(DataWriter writer, ElementBatch batch) throws Exception {
      elementWriter.writeBatch(writer, batch);
   }
}
