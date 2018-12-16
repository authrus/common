package com.authrus.message.invoke;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.message.invoke.Invocation;
import com.authrus.message.serialize.BinaryMarshaller;

public class InvocationBinaryMarshallerTest extends TestCase {

   public void testMarshaller() throws Exception {
      BinaryMarshaller marshaller = new BinaryMarshaller("object");
      Invocation invocation = new Invocation(String.class, "java.lang.String.toString()", new Serializable[] { HashMap.class, "blah" });
      Map<String, Object> attributes = marshaller.fromObject(invocation);

      assertNotNull(attributes.get("object"));
      System.err.println(attributes.get("object"));

      Invocation recovered = (Invocation) marshaller.toObject(attributes);
      assertNotNull(recovered);
      assertEquals(recovered.getType(), String.class);
      assertEquals(recovered.getArguments()[0], HashMap.class);
      assertEquals(recovered.getArguments()[1], "blah");

   }
}
