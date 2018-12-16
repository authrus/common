package com.authrus.message.serialize;

import java.util.HashMap;
import java.util.Map;

import com.authrus.message.bind.ObjectMarshaller;

/**
 * This marshaller leverages the stock serialization to marshal any
 * object. When serialized the resulting byte sequence is encoded with
 * the {@link Base64Encoder} so that it can be converted in to a UTF-8
 * string. Use of this is recommended when an object does not fit well
 * with key value pair attribute mappings.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.message.serialize.AttributeMarshaller
 */
public class BinaryMarshaller implements ObjectMarshaller<Object> {

   private final BinarySerializer serializer;
   private final String attribute;

   public BinaryMarshaller(String attribute) {
      this.serializer = new BinarySerializer();
      this.attribute = attribute;
   }

   @Override
   public Map<String, Object> fromObject(Object object) {
      Map<String, Object> attributes = new HashMap<String, Object>();

      try {
         String text = serializer.toString(object);

         if (text != null) {
            attributes.put(attribute, text);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not serialize", e);
      }
      return attributes;
   }

   @Override
   public Object toObject(Map<String, Object> message) {
      try {
         String text = (String) message.get(attribute);

         if (text != null) {
            return serializer.fromString(text);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not deserialize", e);
      }
      return null;
   }

}
