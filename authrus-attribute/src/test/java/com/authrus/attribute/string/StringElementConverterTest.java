package com.authrus.attribute.string;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.attribute.string.StringElementConverter;
import com.authrus.attribute.string.StringMarshaller;

public class StringElementConverterTest extends TestCase {
   
   public static class ExampleChild implements Serializable{
      private final String key;
      private final String value;
      public ExampleChild(String key, String value){
         this.key = key;
         this.value = value;
      }
   }
   
   public static class ExampleObject implements Serializable {
      private final ExampleChild child;
      private final String value;
      private final int age;
      public ExampleObject(ExampleChild child, String value, int age) {
         this.child = child;
         this.value = value;
         this.age = age;
      }
   }
   
   public static class ExampleRoot implements Serializable {
     private final List<ExampleObject> list;
     private final String name;
     private final long value;
     public ExampleRoot(List<ExampleObject> list, String name, long value) {
        this.list = list;
        this.value = value;
        this.name = name;
     }
   }
   
   public void testWriter() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      List<ExampleObject> list = new ArrayList<ExampleObject>();
      
      for(int i = 0; i < 10; i++) {
         ExampleChild child = new ExampleChild("key-" + i, "value-" + i);
         ExampleObject object = new ExampleObject(child, "name-" + i, i);
         list.add(object);
      }
      ExampleRoot root = new ExampleRoot(list, "x", 245);
      StringMarshaller marshaller = new StringMarshaller(serializer);
      Map<String, String> values = marshaller.toMessage(root);
      StringElementConverter converter = new StringElementConverter(values);
      StringWriter buffer = new StringWriter();
      
      converter.write(buffer);     
      
      String result = buffer.toString();
      System.err.println(result);
      
      Map<String, String> recovered = new HashMap<String, String>();
      StringElementConverter consumer = new StringElementConverter(recovered);
      StringReader reader = new StringReader(result);
      
      consumer.read(reader);  
      
      ExampleRoot recoveredRoot = marshaller.fromMessage(recovered);
      Set<String> keys = recovered.keySet();
      
      for(String key : keys) {
         System.err.println(key + "=" + recovered.get(key));         
      }
      assertNotNull(recoveredRoot);
      assertNotNull(recoveredRoot.list);
      assertEquals(recoveredRoot.list.size(), 10);
      
      for(int i = 0; i < 10; i++) {
         ExampleObject a = recoveredRoot.list.get(i);
         ExampleObject b = root.list.get(i);
         
         assertEquals(a.age, b.age);
         assertEquals(a.value, b.value);
         assertEquals(a.child.key, b.child.key);
         assertEquals(a.child.value, b.child.value);
      }
   }

}
