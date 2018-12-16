package com.authrus.message.serialize;

import java.util.HashMap;
import java.util.Map;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.MapReader;
import com.authrus.attribute.MapWriter;
import com.authrus.message.bind.ObjectMarshaller;

/**
 * This marshaller can be used to convert any serializable object to a
 * map of key value pairs. The {@link java.io.Serializable} interface 
 * must be implemented by the object so that it can be instantiated using
 * the stock serialization tools as a factory. All rules of serialization
 * apply, including the significance of the <code>transient<code> keyword. 
 * <p>
 * The only exception to the normal serialization process is that object
 * cycles are not supported. A {@link CycleFoundException} is thrown if
 * there is a cycle in the object graph. Also, any {@link java.util.Map}
 * must use primitive keys, such as strings, integers, and longs. Two 
 * exceptions to this are the use of enums and classes.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.message.serialize.BinaryMarshaller
 */
public class AttributeMarshaller implements ObjectMarshaller<Object> {

   private final AttributeSerializer serializer;

   public AttributeMarshaller(AttributeSerializer serializer) {
      this.serializer = serializer;
   }

   @Override
   public Map<String, Object> fromObject(Object object) {
      Map<String, Object> map = new HashMap<String, Object>();
      MapWriter writer = new MapWriter(map);

      try {
         serializer.write(object, writer);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create message", e);
      }
      return map;
   }

   @Override
   public Object toObject(Map<String, Object> row) {
      MapReader reader = new MapReader(row);

      try {
         return serializer.read(reader);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create object", e);
      }
   }

}
