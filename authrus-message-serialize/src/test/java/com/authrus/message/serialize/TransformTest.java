package com.authrus.message.serialize;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.authrus.attribute.AttributeSerializer;
import com.authrus.attribute.CombinationBuilder;
import com.authrus.attribute.ObjectBuilder;
import com.authrus.attribute.ReflectionBuilder;
import com.authrus.attribute.SerializationBuilder;
import com.authrus.message.serialize.AttributeMarshaller;

import junit.framework.TestCase;

public class TransformTest extends TestCase {
   
   private static class ExampleObject implements Serializable {
      Date date;
      Locale locale;
      String text;
      double v;
      
      public ExampleObject(Date date, Locale locale, String text){
         this.date = date;
         this.locale = locale;
         this.text = text;
      }
   }
   
   public void testTransform() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      AttributeMarshaller marshaller = new AttributeMarshaller(serializer);
      Date date = new Date();
      Locale locale = new Locale("en", "US");
      ExampleObject object = new ExampleObject(date, locale, "blah");      
      Map<String, Object> map = marshaller.fromObject(object);
      
      System.err.println(map);   
      
      ExampleObject recovered = (ExampleObject)marshaller.toObject(map);
      
      assertEquals(recovered.date, object.date);
      assertEquals(recovered.locale, object.locale);
      assertEquals(recovered.text, object.text);
      assertEquals(recovered.v, object.v);      
   }

}
