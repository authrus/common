package com.authrus.attribute;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.AttributeWriter;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.MapWriter;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;

import junit.framework.TestCase;

public class CycleDetectionTest extends TestCase {

   public static class X implements Serializable {
      private Y y;

      public X(Y y) {
         this.y = y;
      }
   }

   public static class Y implements Serializable {
      private Z z;

      public Y(Z z) {
         this.z = z;
      }
   }

   public static class Z implements Serializable {
      private X x;

      public Z() {
         super();
      }

      public void setX(X x) {
         this.x = x;
      }
   }

   public void testCycleDetection() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new SerializationBuilder());
      sequence.add(new ReflectionBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);
      boolean detected = false;

      Z z = new Z();
      Y y = new Y(z);
      X x = new X(y);
      z.setX(x);

      try {
         serializer.write(x, writer);
      } catch (Exception e) {
         e.printStackTrace();
         detected = true;
      }
      assertTrue("Cycle should have been detected", detected);
   }

}
