package com.authrus.attribute;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.authrus.attribute.transform.ObjectTransformer;

public class ObjectNodeBuilder {

   private final ObjectTransformer transformer;
   private final PrimitiveConverter converter;
   private final ObjectTypeChecker checker;
   private final SectionGenerator generator;
   private final ObjectScanner scanner;
   private final ObjectBuilder builder;

   public ObjectNodeBuilder(ObjectScanner scanner, ObjectBuilder builder, ClassResolver resolver) {
      this.converter = new PrimitiveConverter();
      this.transformer = new ObjectTransformer();
      this.checker = new ObjectTypeChecker(transformer);
      this.generator = new SectionGenerator(resolver);
      this.scanner = scanner;
      this.builder = builder;
   }

   public ObjectNode createNode(Class type) {
      Class actual = converter.convert(type);

      if (checker.isPrimitive(actual)) {
         return createPrimitiveNode(actual);
      }
      if (checker.isTransform(actual)) {
         return createPrimitiveNode(actual);
      }
      if (checker.isArray(actual)) {
         return createArrayNode(actual);
      }
      if (checker.isMap(type)) {
         return createMapNode(actual);
      }
      if (checker.isCollection(type)) {
         return createCollectionNode(actual);
      }
      return createCompositeNode(actual);
   }

   private ObjectNode createPrimitiveNode(Class type) {
      return new PrimitiveNode(type);
   }

   private ObjectNode createArrayNode(Class type) {
      return new ArrayNode(generator, transformer, builder, type);
   }

   private ObjectNode createMapNode(Class type) {
      return new MapNode(generator, transformer, builder, type);
   }

   private ObjectNode createCollectionNode(Class type) {
      return new CollectionNode(generator, transformer, builder, type);
   }

   private ObjectNode createCompositeNode(Class type) {
      List<FieldAccessor> fields = extractFields(type);

      if (!Serializable.class.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Class is not serializable " + type);
      }
      return new CompositeNode(generator, transformer, builder, fields, type);
   }

   private List<FieldAccessor> extractFields(Class type) {
      Map<String, Class> names = new LinkedHashMap<String, Class>();
      List<FieldAccessor> fields = new ArrayList<FieldAccessor>();

      while (type != null) {
         Field[] list = type.getDeclaredFields();

         for (Field field : list) {
            String name = field.getName();
            int modifier = field.getModifiers();

            if (!Modifier.isTransient(modifier) && !Modifier.isStatic(modifier)) {
               Class owner = names.get(name);

               if (owner != null) {
                  throw new IllegalArgumentException("Field " + name + " in " + type + " is shadowed in " + owner);
               }
               FieldAccessor accessor = createAccessor(field);

               field.setAccessible(true);
               fields.add(accessor);
            }
         }
         type = type.getSuperclass();
      }
      return fields;
   }

   private FieldAccessor createAccessor(Field field) {
      Class[] dependents = ClassIntrospector.getDependents(field);
      Class type = field.getType();
      Class actual = converter.convert(type);
      boolean primitive = checker.isPrimitive(actual);
      boolean transform = checker.isTransform(actual);

      if (actual.isArray()) {
         Class element = actual.getComponentType();

         if (element != null) {
            dependents = new Class[] { element };
         };
      }
      return new FieldAccessor(scanner, field, actual, dependents, primitive, transform);
   }
}
