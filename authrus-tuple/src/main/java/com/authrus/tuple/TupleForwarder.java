package com.authrus.tuple;

import java.util.Map;

public class TupleForwarder extends TupleAdapter {
   
   private final TuplePublisher publisher;
   private final String attribute;
   private final String source;
   
   public TupleForwarder(TuplePublisher publisher) {
      this(publisher, null, null);
   }
   
   public TupleForwarder(TuplePublisher publisher, String attribute, String source) {
      this.publisher = publisher;
      this.attribute = attribute;
      this.source = source;
   }
   
   @Override
   public void onUpdate(Tuple tuple) {
      Map<String, Object> attributes = tuple.getAttributes();
      
      if(attribute != null) {
         Object value = attributes.get(attribute);
         
         if(value == null) {
            attributes.put(attribute, source);
         }
      }
      publisher.publish(tuple);
   }
}
