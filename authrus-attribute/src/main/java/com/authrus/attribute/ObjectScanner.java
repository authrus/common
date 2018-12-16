package com.authrus.attribute;

import java.util.HashMap;
import java.util.Map;

public class ObjectScanner {

   private volatile Map<Class, ObjectNode> registry;
   private volatile ObjectNodeAllocator allocator;
   private volatile ObjectNodeBuilder builder;
   private volatile ClassResolver resolver;

   public ObjectScanner(ObjectBuilder builder) {
      this(builder, 100000);
   }
   
   public ObjectScanner(ObjectBuilder builder, int capacity) {
      this.resolver = new ClassResolver(capacity);
      this.registry = new HashMap<Class, ObjectNode>();
      this.builder = new ObjectNodeBuilder(this, builder, resolver);
      this.allocator = new ObjectNodeAllocator(capacity);
   }

   public ObjectGraph createGraph() {
      return new ObjectGraph(this, resolver);
   }

   public ObjectNode createNode(Class type) {
      ObjectNode result = registry.get(type);

      if (result == null) {
         return allocator.allocateNode(type);
      }
      return result;
   }

   private class ObjectNodeAllocator {
      
      private final int capacity;
      
      public ObjectNodeAllocator(int capacity) {
         this.capacity = capacity;
      }
      
      public synchronized ObjectNode allocateNode(Class type) {
         int size = registry.size();
         
         if(size >= capacity) {
            throw new IllegalStateException("Capacity of " + capacity + " has been exceeded");
         }
         ObjectNode node = builder.createNode(type);         
         
         if(node == null) {
            throw new IllegalStateException("Unable to create node for " + type);
         }
         Map<Class, ObjectNode> copy = new HashMap<Class, ObjectNode>();
         
         copy.putAll(registry);
         copy.put(type, node);
         registry = copy;
         
         return node;            
      }
   }
}
