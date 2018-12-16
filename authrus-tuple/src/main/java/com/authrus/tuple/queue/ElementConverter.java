package com.authrus.tuple.queue;

import static java.util.Collections.EMPTY_MAP;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.authrus.tuple.Tuple;

class ElementConverter {

   private final KeyTable table;
   private final String name;

   public ElementConverter(String name) {
      this(name, 10000);
   }
   
   public ElementConverter(String name, int capacity) {
      this.table = new KeyTable(capacity);
      this.name = name;
   }

   public Element toElement(Tuple tuple) {
      Map<String, Object> attributes = tuple.getAttributes();
      String type = tuple.getType();
      
      if(!type.equals(name)) {
         throw new IllegalArgumentException("Unable to convert '" + type + "' to '" + name + "'");
      }
      Set<Entry<String, Object>> entries = attributes.entrySet();
      
      if(!entries.isEmpty()) {
         Map<String, Object> update = new HashMap<String, Object>();
         
         for(Entry<String, Object> entry : entries) {            
            String key = entry.getKey();
            String copy = table.copy(key);
            Object value = entry.getValue();

            update.put(copy, value);
         }
         return new Element(update, type);
      }
      return new Element(EMPTY_MAP, type);
   }

   public Tuple fromElement(Element element) {
      Map<String, Object> attributes = element.getAttributes();
      String type = element.getType();
      
      if(!type.equals(name)) {
         throw new IllegalArgumentException("Unable to convert '" + type + "' to '" + name + "'");
      }
      return new Tuple(attributes, type);   
   }
}
