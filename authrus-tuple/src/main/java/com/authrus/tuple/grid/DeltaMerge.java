package com.authrus.tuple.grid;

public class DeltaMerge {

   private final Schema schema;
   private final Row current;
   private final Row previous;
   private final Key key;
   
   public DeltaMerge(Schema schema, Row current, Row previous, Key key) {
      this.schema = schema;
      this.current = current;
      this.previous = previous;
      this.key = key;
   }
   
   public Key getKey(){
      return key;
   }
   
   public Schema getSchema() {
      return schema;
   }
   
   public Row getCurrent(){
      return current;
   }
   
   public Row getPrevious() {
      return previous;
   }
   
   @Override
   public String toString() {
      return String.valueOf(current);
   }
}
