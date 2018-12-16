package com.authrus.attribute;

public class SectionWriter implements AttributeWriter {

   private final AttributeWriter writer;
   private final String prefix;

   public SectionWriter(AttributeWriter writer, String prefix) {
      this.prefix = prefix;
      this.writer = writer;
   }

   @Override
   public AttributeWriter writeParent() {
      return writer;
   }

   @Override
   public AttributeWriter writeSection(String name) {
      return new SectionWriter(this, name);
   }

   @Override
   public AttributeWriter writeInt(String name, Integer value) {
      writer.writeInt(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeLong(String name, Long value) {
      writer.writeLong(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeByte(String name, Byte value) {
      writer.writeByte(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeShort(String name, Short value) {
      writer.writeShort(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeEnum(String name, Enum value) {
      writer.writeEnum(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeString(String name, String value) {
      writer.writeString(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeChar(String name, Character value) {
      writer.writeChar(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeBoolean(String name, Boolean value) {
      writer.writeBoolean(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeFloat(String name, Float value) {
      writer.writeFloat(prefix.concat(name), value);
      return this;
   }

   @Override
   public AttributeWriter writeDouble(String name, Double value) {
      writer.writeDouble(prefix.concat(name), value);
      return this;
   }
}
