package com.authrus.attribute;

import java.util.Iterator;

/**
 * An attribute reader is used to read key value pairs in a structured manner.
 * Typically this will wrap a {@link java.util.HashMap} and extract the values
 * read from that underlying map.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.attribute.MapReader
 */
public interface AttributeReader {
   AttributeReader readParent();
   AttributeReader readSection(String name);  
   Integer readInt(String name);
   Object readValue(String name);
   Long readLong(String name);
   Byte readByte(String name);
   Short readShort(String name);
   Enum readEnum(String name, Class type);
   String readString(String name);
   Character readChar(String name);
   Boolean readBoolean(String name);
   Float readFloat(String name);
   Double readDouble(String name);
   Iterator<String> readKeys(String name);
   Iterator<Integer> readIndexes(String name);
   Iterator<String> readChildren(String name);
}
