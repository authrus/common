package com.authrus.attribute;

import java.util.Iterator;
import java.util.Map;

public class MapReader implements AttributeReader {

   private final Map<String, Object> attributes;
   private final PrefixFilter prefixes;
   private final IndexFilter indexes;
   private final KeyFilter keys;

   public MapReader(Map<String, Object> attributes) {
      this.prefixes = new PrefixFilter(attributes);
      this.indexes = new IndexFilter(attributes);
      this.keys = new KeyFilter(attributes);
      this.attributes = attributes;
   }

   @Override
   public AttributeReader readParent() {
      return this;
   }

   @Override
   public AttributeReader readSection(String name) {
      if (!name.equals(".")) {
         return new SectionReader(this, name);
      }
      return this;
   }
   
   @Override
   public Object readValue(String name) {
      try {
         return attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }         
   }

   @Override
   public Integer readInt(String name) {
      try {
         return (Integer) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }         
   }

   @Override
   public Long readLong(String name) {
      try {
         return (Long)attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public Byte readByte(String name) {
      try {
         return (Byte)attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public Short readShort(String name) {
      try {
         return (Short) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }      
   }

   @Override
   public Enum readEnum(String name, Class type) {
      String value = readString(name);

      try {
         if(value != null) {
            return Enum.valueOf(type, value);
         }
         return null;
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public String readString(String name) {
      try {
         return (String) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public Character readChar(String name) {
      try {
         return (Character) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public Boolean readBoolean(String name) {
      try {
         return (Boolean) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }
   }

   @Override
   public Float readFloat(String name) {
      try {
         return (Float) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }      
   }

   @Override
   public Double readDouble(String name) {
      try {
         return (Double) attributes.get(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read attribute '" + name + "'", e);
      }      
   }
   
   @Override
   public Iterator<String> readChildren(String name) {
      try {  
         return prefixes.readChildren(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read children for '" + name + "'", e);
      }
   }

   @Override
   public Iterator<String> readKeys(String name) {
      try {  
         return keys.readKeys(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read keys for '" + name + "'", e);
      }
   }

   @Override
   public Iterator<Integer> readIndexes(String name) {
      try {
         return indexes.readIndexes(name);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read indexes for '" + name + "'", e);
      }
   }
   
   @Override
   public String toString() {
      return String.valueOf(attributes);
   }
}
