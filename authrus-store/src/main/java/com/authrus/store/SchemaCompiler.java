package com.authrus.store;

import java.lang.reflect.Field;

import com.zuooh.tuple.grid.Structure;

public class SchemaCompiler {

   public <T> Schema<T> compile(Class<T> type) {
      String entity = type.getSimpleName(); 
      Class base = type;
   
      if(!type.isAnnotationPresent(Entity.class)) {
         throw new IllegalStateException("No entity annotation present for " + type);
      }
      while(base != null) {
         Field[] fields = base.getDeclaredFields();
         
         for(Field field : fields) {
            String name = field.getName();
            
            if(field.isAnnotationPresent(PrimaryKey.class)) {
               String[] keys = new String[]{ name};
               Structure structure = new Structure(keys);
               
               return new Schema<>(structure, type, entity, name);
            }
         }
         base = base.getSuperclass();
      }
      throw new IllegalStateException("No primary key for " + type);
   }
}
