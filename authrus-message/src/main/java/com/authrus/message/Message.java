package com.authrus.message;

import java.util.Collections;
import java.util.Map;

/**
 * A message is basically an envelope for an object that has optional
 * attributes. These attributes can contain a message identifier or 
 * perhaps some information useful for routing the packet. Attribute 
 * values must be primitives or strings only.
 * 
 * @author Niall Gallagher
 */
public class Message {

   private final Map<String, Object> attributes;
   private final Object value;

   public Message(Object value) {
      this(value, Collections.EMPTY_MAP);
   }

   public Message(Object value, Map<String, Object> attributes) {
      this.attributes = attributes;
      this.value = value;
   }

   public Map<String, Object> getAttributes() {
      return attributes;
   }

   public Object getValue() {
      return value;
   }
   
   @Override
   public String toString() {
      return String.valueOf(attributes);
   }
}
