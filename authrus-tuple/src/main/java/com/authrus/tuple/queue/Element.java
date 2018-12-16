package com.authrus.tuple.queue;

import java.util.Map;

import com.authrus.predicate.Argument;

/**
 * An element is an abstraction used to represent an element within queue
 * message queue. This is used to hold the attributes that are associated 
 * with the message and the type of object.
 * 
 * @author Niall Gallagher
 */
public class Element implements Argument {

   private final Map<String, Object> attributes;
   private final String type;

   public Element(Map<String, Object> attributes, String type) {
      this.attributes = attributes;
      this.type = type;
   }

   @Override
   public Object getValue(String name) {
      return attributes.get(name);
   }

   public Map<String, Object> getAttributes() {
      return attributes;
   }

   public String getType() {
      return type;
   }

   @Override
   public String toString() {
      return String.format("%s: [%s]", type, attributes);
   }
}
