package com.authrus.attribute.string;

import java.util.Map;

import com.authrus.attribute.AttributeWriter;
import com.authrus.attribute.SectionWriter;

public class StringMapWriter implements AttributeWriter {

   private final Map<String, String> attributes;

   public StringMapWriter(Map<String, String> attributes) {
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

   public AttributeWriter writeValue(String name, Object value) {
      if (value != null) {
         String text = String.valueOf(value);
         attributes.put(name, text);
      }
      return this;
   }

   @Override
   public AttributeWriter writeInt(String name, Integer value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeLong(String name, Long value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeByte(String name, Byte value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeShort(String name, Short value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeEnum(String name, Enum value) {
      return writeValue(name, value.name());
   }

   @Override
   public AttributeWriter writeString(String name, String value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeChar(String name, Character value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeBoolean(String name, Boolean value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeFloat(String name, Float value) {
      return writeValue(name, value);
   }

   @Override
   public AttributeWriter writeDouble(String name, Double value) {
      return writeValue(name, value);
   }
}
