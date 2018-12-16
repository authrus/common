package com.authrus.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

public class ArrayOfPrimitivesTest extends TestCase {

   public static class ExampleArrayObject implements Serializable {
      int[] array;     
      float[] floaters;
      List<Integer> list;

      public ExampleArrayObject(int[] array, float[] floaters) {
         this.list = new ArrayList<Integer>();
         this.floaters = floaters;
         this.array = array;
         for(int i = 0; i < array.length; i++) {
            list.add(array[i]);
         }
      }
   }

   public void testArrayOfObjects() throws Exception {
      Set<ObjectBuilder> sequence = new LinkedHashSet<ObjectBuilder>();
      sequence.add(new ReflectionBuilder());
      sequence.add(new SerializationBuilder());
      ObjectBuilder factory = new CombinationBuilder(sequence);
      AttributeSerializer serializer = new AttributeSerializer(factory);
      int[] array = new int[]{1, 3, 46, 547, 2, 12, 63456, 47, 457, 4};
      float[] floaters = new float[]{1.2f,66f};
      ExampleArrayObject object = new ExampleArrayObject(array, floaters);
      AttributeConverter marshaller = new AttributeConverter(serializer);
      Map<String, Object> message = marshaller.fromObject(object);
      System.err.println(new TreeMap<String, Object>(message).toString());
      ExampleArrayObject recoveredObject = (ExampleArrayObject) marshaller.toObject(message);

      for(int i = 0; i < array.length; i++) {
         assertEquals(recoveredObject.array[i], object.array[i]);       
      }
      for(int i = 0; i < floaters.length; i++) {
         assertEquals(recoveredObject.floaters[i], object.floaters[i]);           
      }
      Map<String, Object> values = new LinkedHashMap<String, Object>();
      AttributeWriter writer = new MapWriter(values);
      
      serializer.write(recoveredObject, writer);
      
      for(int i = 0; i < array.length; i++) {
         assertEquals(values.get("array["+i+"]"), object.array[i]);
         assertEquals(values.get("list["+i+"]"), object.list.get(i));      
         assertEquals(values.get("array["+i+"]"), recoveredObject.array[i]);
         assertEquals(values.get("list["+i+"]"), recoveredObject.list.get(i));        
      }
      for(int i = 0; i < floaters.length; i++) {
         assertEquals(values.get("floaters["+i+"]"), object.floaters[i]);             
         assertEquals(values.get("floaters["+i+"]"), recoveredObject.floaters[i]);    
      }
      values.clear();      
      serializer.write(object, writer);
      
      for(int i = 0; i < array.length; i++) {
         assertEquals(values.get("array["+i+"]"), recoveredObject.array[i]);
         assertEquals(values.get("list["+i+"]"), recoveredObject.list.get(i));  
       
         assertEquals(values.get("array["+i+"]"), object.array[i]);
         assertEquals(values.get("list["+i+"]"), object.list.get(i));   
             
      }      
      for(int i = 0; i < floaters.length; i++) {
         assertEquals(values.get("floaters["+i+"]"), recoveredObject.floaters[i]);              
         assertEquals(values.get("floaters["+i+"]"), object.floaters[i]); 
      }
      System.err.println(values);
   }
}
