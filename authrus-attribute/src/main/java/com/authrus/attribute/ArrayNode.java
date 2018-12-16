package com.authrus.attribute;

import java.util.Collections;
import java.util.List;

import com.authrus.attribute.transform.ObjectTransformer;

public class ArrayNode implements ObjectNode {

   private final ObjectTransformer transformer;
   private final SectionGenerator generator;
   private final ObjectBuilder builder;
   private final Class type;

   public ArrayNode(SectionGenerator generator, ObjectTransformer transformer, ObjectBuilder builder, Class type) {
      this.transformer = transformer;
      this.generator = generator;
      this.builder = builder;
      this.type = type;
   }

   @Override
   public boolean isTransform() {
      return false;
   }

   @Override
   public boolean isPrimitive() {
      return false;
   }

   @Override
   public List<FieldAccessor> getFields() {
      return Collections.emptyList();
   }

   @Override
   public Converter getConverter(ObjectGraph graph) {
      return new ArrayConverter(generator, graph, transformer, this, type);
   }

   @Override
   public Object getInstance() {
      return getInstance(type);
   }

   @Override
   public Object getInstance(Class type) {
      return builder.createInstance(type);
   }

   @Override
   public Class getType() {
      return type;
   }
}
