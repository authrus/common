package com.authrus.attribute;

import java.util.Collection;
import java.util.Iterator;

import com.authrus.attribute.transform.ObjectTransformer;

public class CollectionConverter extends CompositeConverter {

   public CollectionConverter(SectionGenerator generator, ObjectGraph graph, ObjectTransformer transformer, ObjectNode node, Class type) {
      super(generator, graph, transformer, node, type);
   }

   @Override
   public void readAttributes(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      if (dependents.length < 1) {
         throw new IllegalArgumentException("The collection " + name + " of " + type + " has no generics");
      }
      readCollection(reader, object, name, dependents);
   }

   protected void readCollection(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      AttributeReader parent = reader.readParent();
      Iterator<Integer> indexes = parent.readIndexes(name);

      if (indexes.hasNext()) {
         ObjectNode node = graph.createNode(dependents[0]);
         Collection list = (Collection) object;

         if (!node.isPrimitive()) {
            while (indexes.hasNext()) {
               Integer index = indexes.next();
               String token = generator.generateIndex(index);
               AttributeReader section = reader.readSection(token);
               Converter converter = node.getConverter(graph);
               Object value = node.getInstance();

               converter.readAttributes(section, value, null, null);
               list.add(value);
            }
         } else {
            while (indexes.hasNext()) {
               Integer index = indexes.next();
               String token = generator.generateIndex(index);
               Object value = readAttribute(reader, token, dependents[0]);

               if (value != null) {
                  list.add(value);
               }
            }
         }
      }
   }

   @Override
   public void writeAttributes(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception {
      if (dependents.length < 1) {
         throw new IllegalArgumentException("The collection " + name + " of " + type + " has no generics");
      }
      writeCollection(writer, object, name, dependents);
   }

   protected void writeCollection(AttributeWriter writer, Object object, String name, Class[] dependents) throws Exception {
      ObjectNode node = graph.createNode(dependents[0]);
      Collection list = (Collection) object;
      int index = 0;

      if (!node.isPrimitive()) {
         for (Object value : list) {
            if (value != null) {
               Converter converter = node.getConverter(graph);
               String token = generator.generateIndex(index++);
               AttributeWriter section = writer.writeSection(token);

               converter.writeAttributes(section, value, null, null);
            }
         }
      } else {
         for (Object value : list) {
            String token = generator.generateIndex(index++);

            if (value != null) {
               writeAttribute(writer, token, value);
            }
         }
      }
   }

}
