package com.authrus.tuple.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.authrus.io.DataFormatter;
import com.authrus.io.DataWriter;

public class ElementWriter {

   private Map<String, Integer> references;
   private DataFormatter formatter;
   private AtomicInteger index;
   private String[] indexes;

   public ElementWriter(DataFormatter formatter) {
      this(formatter, 5000);
   }
   
   public ElementWriter(DataFormatter formatter, int capacity) {
      this.references = new HashMap<String, Integer>();
      this.indexes = new String[capacity];  
      this.index = new AtomicInteger();  
      this.formatter = formatter;
   }

   public void writeBatch(DataWriter writer, ElementBatch batch) throws Exception {
      int size = batch.size();

      writer.writeInt(size);

      for (Element element : batch) {
         writeElement(writer, element);
      }
   }

   private void writeElement(DataWriter writer, Element element) throws Exception {
      Map<String, Object> attributes = element.getAttributes();
      Set<String> keys = attributes.keySet();
      String type = element.getType();
      int size = attributes.size();

      writer.writeString(type);
      writer.writeInt(size);

      for (String key : keys) {
         Object value = attributes.get(key);
         Integer reference = references.get(key);

         if(key == null) {
            throw new IllegalArgumentException("Null key for element " + element);
         }
         if(reference == null) {
            int next = index.getAndIncrement();            
            String current = indexes[next];
            
            references.remove(current);            
            index.compareAndSet(indexes.length - 1, 0);
            references.put(key, next);   
            writer.writeBoolean(true);
            writer.writeInt(next);
            writer.writeString(key);         
         } else {
            writer.writeBoolean(false);
            writer.writeInt(reference);
         }
         formatter.write(writer, value);
      }
   }
}
