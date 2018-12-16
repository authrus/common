package com.authrus.attribute;

import java.util.Map;

public class MapWriter implements AttributeWriter {

   private final Map<String, Object> attributes;

   public MapWriter(Map<String, Object> attributes) {
      this.attributes = attributes;
   }

   @Override
   public AttributeWriter writeParent() {
      return this;
   }

   @Override
   public AttributeWriter writeSection(String name) {
      if (!name.equals(".")) {
         return new SectionWriter(this, name);
      }
      return this;
   }

   @Override
   public AttributeWriter writeInt(String name, Integer value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeLong(String name, Long value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeByte(String name, Byte value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeShort(String name, Short value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeEnum(String name, Enum value) {
      if (value != null) {
         attributes.put(name, value.name());
      }
      return this;
   }

   @Override
   public AttributeWriter writeString(String name, String value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeChar(String name, Character value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeBoolean(String name, Boolean value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeFloat(String name, Float value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }

   @Override
   public AttributeWriter writeDouble(String name, Double value) {
      if (value != null) {
         attributes.put(name, value);
      }
      return this;
   }
   
   @Override
   public String toString() {
      return String.valueOf(attributes);
   }
}
