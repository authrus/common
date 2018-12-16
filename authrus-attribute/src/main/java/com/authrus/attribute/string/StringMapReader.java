package com.authrus.attribute.string;

import java.util.Iterator;
import java.util.Map;

import com.authrus.attribute.AttributeReader;
import com.authrus.attribute.IndexFilter;
import com.authrus.attribute.KeyFilter;
import com.authrus.attribute.PrefixFilter;
import com.authrus.attribute.SectionReader;

public class StringMapReader implements AttributeReader {

   private final Map<String, String> attributes;
   private final PrefixFilter prefixes;
   private final IndexFilter indexes;
   private final KeyFilter keys;

   public StringMapReader(Map<String, String> attributes) {
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
      return attributes.get(name);
   }

   @Override
   public Long readLong(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Long.parseLong(value);
      }
      return null;
   }

   @Override
   public Byte readByte(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Byte.parseByte(value);
      }
      return null;
   }

   @Override
   public Short readShort(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Short.parseShort(value);
      }
      return null;
   }

   @Override
   public Enum readEnum(String name, Class type) {
      String value = attributes.get(name);

      if (value != null) {
         return Enum.valueOf(type, value);
      }
      return null;
   }

   @Override
   public Character readChar(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return value.charAt(0);
      }
      return null;
   }

   @Override
   public Boolean readBoolean(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Boolean.parseBoolean(value);
      }
      return null;
   }

   @Override
   public Float readFloat(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Float.parseFloat(value);
      }
      return null;
   }

   @Override
   public Double readDouble(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Double.parseDouble(value);
      }
      return null;
   }

   @Override
   public Integer readInt(String name) {
      String value = attributes.get(name);

      if (value != null) {
         return Integer.parseInt(value);
      }
      return null;
   }

   @Override
   public String readString(String name) {
      return attributes.get(name);
   }
   
   @Override
   public Iterator<String> readChildren(String name) {
      return prefixes.readChildren(name);
   }

   @Override
   public Iterator<String> readKeys(String name) {
      return keys.readKeys(name);
   }

   @Override
   public Iterator<Integer> readIndexes(String name) {
      return indexes.readIndexes(name);
   }
}
