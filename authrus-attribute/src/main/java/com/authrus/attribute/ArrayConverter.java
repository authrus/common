package com.authrus.attribute;

import java.lang.reflect.Array;
import java.util.Iterator;

import com.authrus.attribute.transform.ObjectTransformer;

public class ArrayConverter extends CompositeConverter {

   public ArrayConverter(SectionGenerator generator, ObjectGraph graph, ObjectTransformer transformer, ObjectNode node, Class type) {
      super(generator, graph, transformer, node, type);
   }

   @Override
   public void readAttributes(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      AttributeReader parent = reader.readParent();
      Iterator<Integer> indexes = parent.readIndexes(name);

      if (indexes.hasNext()) {
         Class array = object.getClass();
         Class element = array.getComponentType();
         ObjectNode node = graph.createNode(element);
         Class type = node.getType();         

         if (!node.isPrimitive()) {
            while (indexes.hasNext()) {
               Integer index = indexes.next();
               String token = generator.generateIndex(index);
               AttributeReader section = reader.readSection(token);
               Converter converter = node.getConverter(graph);
               Object value = node.getInstance();

               converter.readAttributes(section, value, null, null);
               Array.set(object, index, value);
            }
         } else {            
            while (indexes.hasNext()) {
               Integer index = indexes.next();           
               String token = generator.generateIndex(index);
               Object value = readAttribute(reader, token, type);

               if (value != null) {
                  Array.set(object, index, value);
               }
            }
         }
      }
   }

   @Override
   public void writeAttributes(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception {
      ObjectNode node = graph.createNode(dependents[0]);
      int length = Array.getLength(object);

      if (!node.isPrimitive()) {
         for (int i = 0; i < length; i++) {
            Object value = Array.get(object, i);

            if (value != null) {
               String token = generator.generateIndex(i);
               Converter converter = node.getConverter(graph);
               AttributeWriter section = writer.writeSection(token);

               converter.writeAttributes(section, value, null, null);
            }
         }
      } else {
         for (int i = 0; i < length; i++) {
            String token = generator.generateIndex(i);
            Object value = Array.get(object, i);

            if (value != null) {
               writeAttribute(writer, token, value);
            }
         }
      }
   }

}
