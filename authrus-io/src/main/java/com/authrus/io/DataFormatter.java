package com.authrus.io;

import static com.authrus.io.DataFormat.BOOLEAN;
import static com.authrus.io.DataFormat.CHARACTER;
import static com.authrus.io.DataFormat.DOUBLE;
import static com.authrus.io.DataFormat.FLOAT;
import static com.authrus.io.DataFormat.INTEGER;
import static com.authrus.io.DataFormat.LONG;
import static com.authrus.io.DataFormat.OCTET;
import static com.authrus.io.DataFormat.SHORT;
import static com.authrus.io.DataFormat.TEXT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFormatter {
   
   private final Map<String, Integer> references;
   private final List<String> indexes;

   public DataFormatter() {
      this.references = new HashMap<String, Integer>();
      this.indexes = new ArrayList<String>();
   }

   public Object read(DataReader reader) throws IOException {
      if (reader.readBoolean()) {
         char code = reader.readChar();
         DataFormat format = DataFormat.resolveFormat(code);

         if (format == INTEGER) {
            return reader.readInt();
         } else if (format == LONG) {
            return reader.readLong();
         } else if (format == FLOAT) {
            return reader.readFloat();
         } else if (format == DOUBLE) {
            return reader.readDouble();
         } else if (format == OCTET) {
            return reader.readByte();
         } else if (format == SHORT) {
            return reader.readShort();
         } else if (format == BOOLEAN) {
            return reader.readBoolean();
         } else if (format == CHARACTER) {
            return reader.readChar();            
         } else if (format == TEXT) {
            int index = reader.readInt();
            int size = indexes.size();
            
            if(index >= size) {
               String value = reader.readString();
               
               if(value != null) {
                  references.put(value, size);               
                  indexes.add(value);
               }
               return value;
            }
            return indexes.get(index);
         }
      }
      return null;
   }

   public void write(DataWriter writer, Object value) throws IOException {
      if (value != null) {
         Class type = value.getClass();
         DataFormat format = DataFormat.resolveFormat(type);

         writer.writeBoolean(true);
         writer.writeChar(format.code);

         if (format == INTEGER) {
            writer.writeInt((Integer) value);
         } else if (format == LONG) {
            writer.writeLong((Long) value);
         } else if (format == FLOAT) {
            writer.writeFloat((Float) value);
         } else if (format == DOUBLE) {
            writer.writeDouble((Double) value);
         } else if (format == OCTET) {
            writer.writeByte((Byte) value);
         } else if (format == SHORT) {
            writer.writeShort((Short) value);
         } else if (format == BOOLEAN) {
            writer.writeBoolean((Boolean) value);
         } else if (format == CHARACTER) {
            writer.writeChar((Character) value);              
         } else if (format == TEXT) {
            String text = (String) value;
            Integer index = references.get(text);
            
            if(index == null) {
               int size = indexes.size();
               
               writer.writeInt(size);
               writer.writeString(text);
               references.put(text, size);
               indexes.add(text);
            } else {               
               writer.writeInt(index);
            }
         }
      } else {
         writer.writeBoolean(false);
      }
   }
   
   public void reset() {
      references.clear();
      indexes.clear();
   }
}
