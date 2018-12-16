package com.authrus.tuple.queue;

import java.io.IOException;

import com.authrus.io.DataConsumer;
import com.authrus.io.DataFormatter;
import com.authrus.io.DataReader;

public class ElementConsumer implements DataConsumer {

   private final DataFormatter formatter;
   private final ElementListener listener;
   private final ElementReader reader;

   public ElementConsumer(ElementListener listener) {
      this.formatter = new DataFormatter();
      this.reader = new ElementReader(formatter);
      this.listener = listener;
   }

   @Override
   public synchronized void consume(DataReader input) throws Exception {
      ElementBatch batch = reader.readBatch(input);

      try {
         consume(input, batch);
      } finally {
         formatter.reset();
      }
   }
   
   private synchronized void consume(DataReader input, ElementBatch batch) throws Exception {
      for (Element element : batch) {
         try {           
            listener.onElement(element);            
         } catch (Exception e) {
            throw new IOException("Could not process element " + element, e);
         }
      }
   }
  
}
