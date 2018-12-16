package com.authrus.attribute;

import java.util.Iterator;

public class SectionReader implements AttributeReader {

   private final AttributeReader reader;
   private final String prefix;

   public SectionReader(AttributeReader reader, String prefix) {
      this.prefix = prefix;
      this.reader = reader;
   }

   @Override
   public AttributeReader readParent() {
      return reader;
   }

   @Override
   public AttributeReader readSection(String name) {
      return new SectionReader(this, name);
   }
   
   @Override
   public Object readValue(String name) {
      return reader.readValue(prefix.concat(name));
   }

   @Override
   public Integer readInt(String name) {
      return reader.readInt(prefix.concat(name));
   }

   @Override
   public Long readLong(String name) {
      return reader.readLong(prefix.concat(name));
   }

   @Override
   public Byte readByte(String name) {
      return reader.readByte(prefix.concat(name));
   }

   @Override
   public Short readShort(String name) {
      return reader.readShort(prefix.concat(name));
   }

   @Override
   public Enum readEnum(String name, Class type) {
      return reader.readEnum(prefix.concat(name), type);
   }

   @Override
   public String readString(String name) {
      return reader.readString(prefix.concat(name));
   }

   @Override
   public Character readChar(String name) {
      return reader.readChar(prefix.concat(name));
   }

   @Override
   public Boolean readBoolean(String name) {
      return reader.readBoolean(prefix.concat(name));
   }

   @Override
   public Float readFloat(String name) {
      return reader.readFloat(prefix.concat(name));
   }

   @Override
   public Double readDouble(String name) {
      return reader.readDouble(prefix.concat(name));
   }
   
   @Override
   public Iterator<String> readChildren(String name) {
      return reader.readChildren(prefix.concat(name));
   }

   @Override
   public Iterator<String> readKeys(String name) {
      return reader.readKeys(prefix.concat(name));
   }

   @Override
   public Iterator<Integer> readIndexes(String name) {
      return reader.readIndexes(prefix.concat(name));
   }
}
