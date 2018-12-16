package com.authrus.attribute;

/**
 * An attribute writer is used to write key value pairs in a structured manner.
 * Typically this will wrap a {@link java.util.HashMap} and populate the values
 * of that underlying map.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.attribute.MapWriter
 */
public interface AttributeWriter {
   AttributeWriter writeParent();
   AttributeWriter writeSection(String name);
   AttributeWriter writeInt(String name, Integer value);
   AttributeWriter writeLong(String name, Long value);
   AttributeWriter writeByte(String name, Byte value);
   AttributeWriter writeShort(String name, Short value);
   AttributeWriter writeEnum(String name, Enum value);
   AttributeWriter writeString(String name, String value);
   AttributeWriter writeChar(String name, Character value);
   AttributeWriter writeBoolean(String name, Boolean value);
   AttributeWriter writeFloat(String name, Float value);
   AttributeWriter writeDouble(String name, Double value);
}
