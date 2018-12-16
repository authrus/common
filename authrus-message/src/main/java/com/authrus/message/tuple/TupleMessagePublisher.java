package com.authrus.message.tuple;

import java.util.Map;

import com.authrus.message.Message;
import com.authrus.message.MessagePublisher;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TuplePublisher;

public class TupleMessagePublisher implements MessagePublisher {
   
   private final TuplePublisher publisher;
   private final ObjectBinder binder;
   
   public TupleMessagePublisher(TuplePublisher publisher, ObjectBinder binder) {
      this.publisher = publisher;
      this.binder = binder;
   }

   @Override
   public void publish(Message message) {
      Object value = message.getValue();
      Class type = value.getClass();
      String name = type.getName();
      Map<String, Object> values = binder.fromObject(value,  name);
      
      if(!values.isEmpty()) {
         Tuple tuple = new Tuple(values, name);
         
         try {
            publisher.publish(tuple);
         } catch(Exception e) {
            throw new IllegalStateException("Could not publish message " + message, e);
         }
      }
   }

}
