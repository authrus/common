package com.authrus.attribute;

import java.lang.reflect.Field;

public class FieldAccessor {

   private final ObjectScanner scanner;
   private final Class[] dependents;
   private final Field field;
   private final String name;
   private final Class type;
   private final boolean primitive;
   private final boolean transform;
   private final boolean array;

   public FieldAccessor(ObjectScanner scanner, Field field, Class type, Class[] dependents, boolean primitive, boolean transform) {
      this.array = type.isArray();
      this.name = field.getName();
      this.primitive = primitive;
      this.transform = transform;
      this.dependents = dependents;
      this.scanner = scanner;
      this.field = field;
      this.type = type;
   }

   public boolean isArray() {
      return array;
   }
   
   public boolean isTransform() {
      return transform;
   }

   public boolean isPrimitive() {
      return primitive;
   }

   public ObjectNode getNode() {
      return scanner.createNode(type);
   }

   public ObjectNode getNode(Class type) {
      return scanner.createNode(type);
   }

   public Field getField() {
      return field;
   }

   public Class[] getDependents() {
      return dependents;
   }

   public Class getType() {
      return type;
   }

   public String getName() {
      return name;
   }
}
