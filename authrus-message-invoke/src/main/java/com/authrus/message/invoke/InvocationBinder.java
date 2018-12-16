package com.authrus.message.invoke;

import java.util.Map;

import com.authrus.message.bind.ObjectBinder;
import com.authrus.message.serialize.BinaryBinder;
import com.authrus.tuple.Tuple;

public class InvocationBinder {
   
   private final ObjectBinder binder;
   
   public InvocationBinder() {
      this.binder = new BinaryBinder("object");
   }
   
   public Tuple toTuple(Object value) {
      Class type = value.getClass();
      String name = type.getName();
      Map<String, Object> attributes = binder.fromObject(value, name);

      if(type == Invocation.class) {
         return new Tuple(attributes, "invoke");
      }
      if(type == ReturnValue.class) {
         return new Tuple(attributes, "return");
      }
      throw new IllegalArgumentException("Invocation does not support " + type);
   }
   
   public Object fromTuple(Tuple tuple) {
      String name = tuple.getType();
      Map<String, Object> attributes = tuple.getAttributes();
      
      return binder.toObject(attributes, name);
   }
}
