package com.authrus.attribute;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class PrimitiveAsObjectTest extends TestCase {
   
   public static class PrimtiveAsObject implements Serializable {
      
      private final Object value;
      private final int x;
      public PrimtiveAsObject(Object value, int x){
         this.value = value;
         this.x = x;
      }
      public Object getValue(){
         return value;
      }
      public int getX(){
         return x;
      }
   }
   
   public void testPrimitiveAsObject() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      AttributeConverter marshaller = new AttributeConverter(serializer);
      PrimtiveAsObject object = new PrimtiveAsObject("blah as text...", 1234);      
      Map<String, Object> map = marshaller.fromObject(object);
      PrimtiveAsObject recovered = (PrimtiveAsObject)marshaller.toObject(map);
      
      assertEquals(recovered.x, object.x);
      assertEquals(recovered.value, object.value);
   }

}
