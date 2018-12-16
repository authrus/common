package com.authrus.attribute.string;

import java.util.Map;
import java.util.TreeMap;

import com.authrus.attribute.AttributeReader;
import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.AttributeWriter;

public class StringMarshaller {

   private final AttributeSerializer serializer;

   public StringMarshaller(AttributeSerializer serializer) {
      this.serializer = serializer;
   }

   public <T> Map<String, String> toMessage(T object) {
      Map<String, String> message = new TreeMap<String, String>();
      AttributeWriter writer = new StringMapWriter(message);

      try {
         serializer.write(object, writer);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create message", e);
      }
      return message;
   }

   public <T> T fromMessage(Map<String, String> message) {
      AttributeReader reader = new StringMapReader(message);

      try {
         return (T) serializer.read(reader);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create object " + message, e);
      }
   }
}
