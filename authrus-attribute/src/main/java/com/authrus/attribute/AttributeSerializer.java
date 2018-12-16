package com.authrus.attribute;

public class AttributeSerializer {

   private final ClassResolver resolver;
   private final ObjectScanner scanner;

   public AttributeSerializer(ObjectBuilder builder) {
      this.scanner = new ObjectScanner(builder);
      this.resolver = new ClassResolver();
   }

   public Object read(AttributeReader reader) throws Exception {
      ObjectGraph graph = scanner.createGraph();
      String name = reader.readString("class");

      if (name == null) {
         throw new IllegalArgumentException("Required 'class' attribute not provided");
      }
      Class type = resolver.resolveClass(name);
      ObjectNode node = graph.createNode(type);
      
      if(node == null) {
         throw new IllegalArgumentException("Could not create node for " + type);
      }
      Converter converter = node.getConverter(graph);
      Object instance = node.getInstance(type);

      if (instance != null) {
         converter.readAttributes(reader, instance, null, null);
      }
      return instance;
   }

   public void write(Object value, AttributeWriter writer) throws Exception {
      ObjectGraph graph = scanner.createGraph();

      if (value == null) {
         throw new IllegalArgumentException("Serialization of null is illegal");
      }
      Class type = value.getClass();
      ObjectNode node = graph.createNode(type);
      
      if(node == null) {
         throw new IllegalArgumentException("Could not create node for " + type);
      }
      Converter converter = node.getConverter(graph);
      String name = type.getName();

      writer.writeString("class", name);
      converter.writeAttributes(writer, value, null, null);
   }
}
