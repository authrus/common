package com.authrus.tuple.queue;

import java.util.HashMap;
import java.util.Map;

import com.authrus.io.DataFormatter;
import com.authrus.io.DataReader;

public class ElementReader {

   private final Map<Integer, String> references;
   private final DataFormatter formatter;

   public ElementReader(DataFormatter formatter) {
      this.references = new HashMap<Integer, String>();
      this.formatter = formatter;
   }

   public ElementBatch readBatch(DataReader reader) throws Exception {
      int size = reader.readInt();

      if (size > 0) {
         ElementBatch batch = new ElementBatch(size);  
         
         for (int i = 0; i < size; i++) {
            Element element = readElement(reader);

            if (element != null) {
               batch.insert(element);
            }
         }
         return batch;
      }
      return new ElementBatch();
   }

   public Element readElement(DataReader reader) throws Exception {
      String type = reader.readString();
      int size = reader.readInt();

      if (size < 0) {
         throw new IllegalStateException("Key count of " + size + " is illegal");
      }
      Map<String, Object> values = new HashMap<String, Object>(size * 2);

      for (int i = 0; i < size; i++) {
         if(reader.readBoolean()) {
            int index = reader.readInt();
            String key = reader.readString();
            Object value = formatter.read(reader);
            
            values.put(key, value);
            references.put(index, key);
         } else {
            int index = reader.readInt();
            String key = references.get(index);
            Object value = formatter.read(reader);

            values.put(key, value);
         }
      }
      return new Element(values, type);
   }
}
