package com.authrus.attribute;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import com.authrus.attribute.transform.ObjectTransform;
import com.authrus.attribute.transform.ObjectTransformer;

public class CompositeConverter implements Converter {

   protected final ObjectTransformer transformer;
   protected final SectionGenerator generator;
   protected final ObjectGraph graph;
   protected final ObjectNode node;
   protected final Class type;

   public CompositeConverter(SectionGenerator generator, ObjectGraph graph, ObjectTransformer transformer, ObjectNode node, Class type) {
      this.transformer = transformer;
      this.generator = generator;
      this.graph = graph;
      this.type = type;
      this.node = node;
   }

   @Override
   public void readAttributes(AttributeReader reader, Object object, String name, Class[] dependents) throws Exception {
      AttributeReader section = reader.readSection(".");
      List<FieldAccessor> accessors = node.getFields();

      for (FieldAccessor accessor : accessors) {
         if (accessor.isArray()) {
            readArray(section, accessor, object);
         } else if (accessor.isPrimitive()) {
            readPrimitive(section, accessor, object);
         } else if(accessor.isTransform()) {
            readTransform(section, accessor, object);
         } else {
            readObject(section, accessor, object);
         }
      }
   }

   protected void readPrimitive(AttributeReader reader, FieldAccessor accessor, Object object) throws Exception {
      Class type = accessor.getType();
      Field field = accessor.getField();
      String name = accessor.getName();
      Object value = readAttribute(reader, name, type);

      if (value != null) {
         field.set(object, value);
      }
   }

   protected void readObject(AttributeReader reader, FieldAccessor accessor, Object object) throws Exception {
      String name = accessor.getName();
      AttributeReader section = reader.readSection(name);

      if (section != null) {
         Field field = accessor.getField();
         String attribute = section.readString(".class");
         
         if(attribute == null) {
            Class type = field.getType();
            Iterator<String> children = reader.readChildren(name);
            
            if(children.hasNext()) {
               attribute = graph.resolveAttribute(type);
            }
         }
         if(attribute != null) {
            Class actual = graph.resolveClass(attribute);
            ObjectNode child = accessor.getNode(actual);
            
            if(!child.isPrimitive()) {
               Object value = child.getInstance(actual);
               Converter converter = child.getConverter(graph);
               Class[] dependents = accessor.getDependents();
   
               converter.readAttributes(section, value, name, dependents);
               field.set(object, value);
            } else {
               Object value = readAttribute(reader, name, actual);
               
               field.set(object, value);               
            }         
         }
      }
   }
   
   protected void readTransform(AttributeReader reader, FieldAccessor accessor, Object object) throws Exception {
      String name = accessor.getName();
      Object attribute = reader.readValue(name);
      
      if(attribute != null) {
         Field field = accessor.getField();
         Class type = accessor.getType();
         ObjectTransform transform = transformer.lookup(type);
         Object value = transform.toObject(attribute);
         
         field.set(object, value);
      }
   }

   protected void readArray(AttributeReader reader, FieldAccessor accessor, Object object) throws Exception {
      String name = accessor.getName();
      AttributeReader section = reader.readSection(name);

      if (section != null) {
         Field field = accessor.getField();
         ObjectNode child = accessor.getNode();
         Class type = field.getType();
         Class element = type.getComponentType();
         Integer length = section.readInt(".length");

         if (length != null) {
            Object array = Array.newInstance(element, length);
            Converter converter = child.getConverter(graph);
            Class[] dependents = accessor.getDependents();

            converter.readAttributes(section, array, name, dependents);
            field.set(object, array);
         }
      }
   }

   @Override
   public void writeAttributes(AttributeWriter writer, Object parent, String name, Class[] dependents) throws Exception {
      AttributeWriter section = writer.writeSection(".");
      List<FieldAccessor> accessors = node.getFields();

      for (FieldAccessor accessor : accessors) {
         if (accessor.isArray()) {
            writeArray(section, accessor, parent);
         } else if (accessor.isPrimitive()) {
            writePrimitive(section, accessor, parent);
         } else if(accessor.isTransform()) {
            writeTransform(section, accessor, parent);
         } else {
            writeObject(section, accessor, parent);
         }
      }
   }

   protected void writePrimitive(AttributeWriter writer, FieldAccessor accessor, Object parent) throws Exception {
      Field field = accessor.getField();
      String name = accessor.getName();
      Object value = field.get(parent);

      if (value != null) {
         writeAttribute(writer, name, value);
      }
   }

   protected void writeObject(AttributeWriter writer, FieldAccessor accessor, Object parent) throws Exception {
      Field field = accessor.getField();
      String name = accessor.getName();
      Object value = field.get(parent);

      if (value != null) {
         Class type = value.getClass();
         ObjectNode child = accessor.getNode(type);
         String attribute = type.getName();
         AttributeWriter section = writer.writeSection(name);
         
         writeAttribute(section, ".class", attribute);
         
         if(!child.isPrimitive()) {
            Converter converter = child.getConverter(graph);
            Class[] dependents = accessor.getDependents();
   
            graph.addNode(value, name);
            converter.writeAttributes(section, value, name, dependents);
            graph.removeNode(value);
         } else {
            writeAttribute(writer, name, value);        
         }
      }
   }
   
   protected void writeTransform(AttributeWriter writer, FieldAccessor accessor, Object parent) throws Exception {
      Field field = accessor.getField();
      String name = accessor.getName();
      Object value = field.get(parent);

      if (value != null) {
         Class type = value.getClass();
         ObjectTransform transform = transformer.lookup(type);
         Object attribute = transform.fromObject(value);

         writeAttribute(writer, name, attribute);
      }
   }

   protected void writeArray(AttributeWriter writer, FieldAccessor accessor, Object parent) throws Exception {
      Field field = accessor.getField();
      String name = accessor.getName();
      Object value = field.get(parent);

      if (value != null) {
         ObjectNode child = accessor.getNode();
         Converter converter = child.getConverter(graph);
         AttributeWriter section = writer.writeSection(name);
         Integer length = Array.getLength(value);
         Class[] dependents = accessor.getDependents();

         graph.addNode(value, name);
         writeAttribute(section, ".length", length);
         converter.writeAttributes(section, value, name, dependents);
         graph.removeNode(value);
      }
   }

   protected Object readAttribute(AttributeReader reader, String name, Class type) throws Exception {
      if (type == String.class) {
         return reader.readString(name);
      } else if (type == Integer.class) {
         return reader.readInt(name);
      } else if (type == Float.class) {
         return reader.readFloat(name);
      } else if (type == Double.class) {
         return reader.readDouble(name);
      } else if (type == Long.class) {
         return reader.readLong(name);
      } else if (type == Short.class) {
         return reader.readShort(name);
      } else if (type == Byte.class) {
         return reader.readByte(name);
      } else if (type == Character.class) {
         return reader.readChar(name);
      } else if (type == Boolean.class) {
         return reader.readBoolean(name);
      } else if (Enum.class.isAssignableFrom(type)) {
         return reader.readEnum(name, type);
      } else {
         throw new IllegalArgumentException("Could not read " + type);
      }
   }

   protected void writeAttribute(AttributeWriter writer, String name, Object value) throws Exception {
      Class type = value.getClass();

      if (type == String.class) {
         writer.writeString(name, (String) value);
      } else if (type == Integer.class) {
         writer.writeInt(name, (Integer) value);
      } else if (type == Float.class) {
         writer.writeFloat(name, (Float) value);
      } else if (type == Double.class) {
         writer.writeDouble(name, (Double) value);
      } else if (type == Long.class) {
         writer.writeLong(name, (Long) value);
      } else if (type == Short.class) {
         writer.writeShort(name, (Short) value);
      } else if (type == Byte.class) {
         writer.writeByte(name, (Byte) value);
      } else if (type == Character.class) {
         writer.writeChar(name, (Character) value);
      } else if (type == Boolean.class) {
         writer.writeBoolean(name, (Boolean) value);
      } else if (Enum.class.isAssignableFrom(type)) {
         writer.writeEnum(name, (Enum) value);
      } else {
         throw new IllegalArgumentException("Could not write " + type);
      }
   }
}
