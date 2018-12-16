package com.authrus.tuple;

import java.util.Map;

public class Tuple {

   private final Map<String, Object> attributes;
   private final String type;
   private final long change;
   
   public Tuple(Map<String, Object> attributes, String type) {
      this(attributes, type, 0);
   }
   
   public Tuple(Map<String, Object> attributes, String type, long change) { 
      this.attributes = attributes;
      this.change = change;
      this.type = type;      
   }
   
   public long getChange() {
      return change;
   }
   
   public String getType() {
      return type;
   }   
   
   public Map<String, Object> getAttributes() {
      return attributes;
   }
   
   @Override
   public String toString() {
      return String.valueOf(attributes);
   }
}
