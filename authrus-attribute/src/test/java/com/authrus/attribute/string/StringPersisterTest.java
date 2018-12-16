package com.authrus.attribute.string;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;




import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.attribute.string.StringPersister;

import junit.framework.TestCase;

public class StringPersisterTest extends TestCase {

   private static class MapMessage implements Serializable {
      Map<String, String> message;

      public MapMessage(Map<String, String> message) {
         this.message = message;
      }
   }

   public void testPersister() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      StringPersister persister = new StringPersister(serializer);
      Map<String, String> message = new HashMap<String, String>();
      MapMessage object = new MapMessage(message);

      message.put("a", "A");
      message.put("b", "Bad property with = and \r\n line feeds");
      message.put("c", "C");
      message.put("d", "D");
      message.put("e", "E");

      String state = persister.toState(object);
      System.err.println(state);

      MapMessage recovered = (MapMessage) persister.fromState(state);

      assertEquals(recovered.message.get("a"), object.message.get("a"));
      assertEquals(recovered.message.get("b"), object.message.get("b"));
      assertEquals(recovered.message.get("c"), object.message.get("c"));
      assertEquals(recovered.message.get("d"), object.message.get("d"));
      assertEquals(recovered.message.get("e"), object.message.get("e"));
   }

}
