package com.authrus.tuple.frame;

import java.util.Map;

import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.queue.Element;
import com.authrus.tuple.queue.ElementBatch;

class SessionPublisher {

   private final SessionDispatcher dispatcher;

   public SessionPublisher(SessionDispatcher dispatcher) {
      this.dispatcher = dispatcher;  
   }
   
   public int publish(Query query) {    
      try {
         return dispatcher.dispatch(query);
      } catch(Exception e) {
         throw new IllegalStateException("Could not dispatch query " + query, e);
      }
   }

   public int publish(Tuple tuple) { 
      try {           
         String type = tuple.getType();        
         Map<String, Object> attributes = tuple.getAttributes();

         if (!attributes.isEmpty()) {
            Element element = new Element(attributes, type);
            ElementBatch batch = new ElementBatch();
         
            batch.insert(element);
         
            return dispatcher.dispatch(batch);
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not dispatch message " + tuple, e);
      }
      return -1;
   }
}
