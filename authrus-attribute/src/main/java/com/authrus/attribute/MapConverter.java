package com.authrus.attribute;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.authrus.attribute.transform.ObjectTransform;
import com.authrus.attribute.transform.ObjectTransformer;

public class MapConverter extends CompositeConverter {

   public MapConverter(SectionGenerator generator, ObjectGraph graph, ObjectTransformer transformer, ObjectNode node, Class type) {
      super(generator, graph, transformer, node, type);
   }

   @Override
   public void readAttributes(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      if (dependents.length < 2) {
         throw new IllegalArgumentException("The map " + name + " of " + type + " has no generics");
      }
      ObjectNode key = graph.createNode(dependents[0]);

      if (!key.isPrimitive()) {
         throw new IllegalArgumentException("Key for " + name + " of " + type + " has an illegal key of " + dependents[0]);
      }
      readMap(reader, object, name, dependents);
   }

   protected void readMap(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      AttributeReader parent = reader.readParent();
      Iterator<String> keys = parent.readKeys(name);

      if (keys.hasNext()) {
         ObjectNode node = graph.createNode(dependents[1]);
         Map map = (Map) object;

         if (!node.isPrimitive()) {
            while(keys.hasNext()) {
               String key = keys.next();
               String token = generator.generateKey(key);
               Object real = generator.generateValue(dependents[0], key);
               AttributeReader section = reader.readSection(token);
               Converter converter = node.getConverter(graph);
               Object instance = node.getInstance();

               converter.readAttributes(section, instance, null, null);
               map.put(real, instance);
            }
         } else {
            ObjectTransform transform = transformer.lookup(dependents[1]);
            
            while(keys.hasNext()) {
               String key = keys.next();
               String token = generator.generateKey(key);
               Object real = generator.generateValue(dependents[0], key);
               
               if(transform != null) {
                  Object value = reader.readValue(token);                  
                  Object actual = transform.toObject(value);
                  
                  if(actual != null) {
                     map.put(real, actual);
                  }
               } else {
                  Object value = readAttribute(reader, token, dependents[1]);

                  if (value != null) {
                     map.put(real, value);
                  }
               }
            }
         }
      }
   }

   @Override
   public void writeAttributes(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception {
      if (dependents.length < 2) {
         throw new IllegalArgumentException("The map " + name + " of " + type + " has no generics");
      }
      ObjectNode key = graph.createNode(dependents[0]);

      if (!key.isPrimitive()) {
         throw new IllegalArgumentException("Key for " + name + " of " + type + " has an illegal key of " + dependents[0]);
      }
      writeMap(writer, object, name, dependents);
   }

   protected void writeMap(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception {
      ObjectNode node = graph.createNode(dependents[1]);
      Map map = (Map) object;
      Set<Entry> entries = map.entrySet();

      if (!node.isPrimitive()) {
         for (Entry entry : entries) {
            Object key = entry.getKey();            
            Object value = entry.getValue();

            if (value != null) {
               String token = generator.generateKey(key);
               AttributeWriter section = writer.writeSection(token);
               Converter converter = node.getConverter(graph);

               converter.writeAttributes(section, value, null, null);
            }
         }
      } else {
         ObjectTransform transform = transformer.lookup(dependents[1]);
         
         for (Entry entry : entries) {
            Object key = entry.getKey();            
            Object value = entry.getValue();
            String token = generator.generateKey(key);
            
            if(transform != null) {
               value = transform.fromObject(value);
            }
            if (value != null) {
               writeAttribute(writer, token, value);
            }
         }
      }
   }
}
