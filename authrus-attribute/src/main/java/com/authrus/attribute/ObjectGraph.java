package com.authrus.attribute;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ObjectGraph {

   private final Map<Object, String> nodes;
   private final ObjectScanner scanner;
   private final ClassResolver resolver;

   public ObjectGraph(ObjectScanner scanner, ClassResolver resolver) {
      this.nodes = new IdentityHashMap<Object, String>();
      this.resolver = resolver;
      this.scanner = scanner;
   }

   public ObjectNode createNode(Class type) {
      return scanner.createNode(type);
   }

   public void addNode(Object value, String name) {
      String existing = nodes.put(value, name);

      if (existing != null) {
         throw new IllegalStateException("Cycle detected for '" + existing + "'");
      }
   }

   public void removeNode(Object value) {
      nodes.remove(value);
   }
   
   public Class resolveClass(String type) {
      try {
         return resolver.resolveClass(type);
      } catch (Exception e) {
         throw new IllegalStateException("Could not resolve " + type, e);
      }
   }

   public String resolveAttribute(Class type) {
      int modifiers = type.getModifiers();
      
      if(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
         if(type.isAssignableFrom(ArrayList.class)) {
            type = ArrayList.class;
         } else if(type.isAssignableFrom(HashSet.class)) {
            type = HashSet.class;                 
         } else if(type.isAssignableFrom(TreeSet.class)) {
            type = TreeSet.class;              
         } else if(type.isAssignableFrom(HashMap.class)) {
            type = HashMap.class;
         } else if(type.isAssignableFrom(TreeMap.class)) {
            type = TreeMap.class;
         } else {
            throw new IllegalStateException("No implementation for " + type);
         }         
      }
      return type.getName(); 
   } 
}
