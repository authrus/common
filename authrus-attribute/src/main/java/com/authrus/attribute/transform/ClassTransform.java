package com.authrus.attribute.transform;

public class ClassTransform implements ObjectTransform<Class, String> {

   public Class toObject(String target) throws Exception {
      Class type = readPrimitive(target);

      if (type == null) {
         ClassLoader loader = getClassLoader();

         if (loader == null) {
            loader = getCallerClassLoader();
         }
         return loader.loadClass(target);
      }
      return type;
   }

   private Class readPrimitive(String target) throws Exception {
      if (target.equals("byte")) {
         return byte.class;
      }
      if (target.equals("short")) {
         return short.class;
      }
      if (target.equals("int")) {
         return int.class;
      }
      if (target.equals("long")) {
         return long.class;
      }
      if (target.equals("char")) {
         return char.class;
      }
      if (target.equals("float")) {
         return float.class;
      }
      if (target.equals("double")) {
         return double.class;
      }
      if (target.equals("boolean")) {
         return boolean.class;
      }
      if (target.equals("void")) {
         return void.class;
      }
      return null;
   }

   public String fromObject(Class target) throws Exception {
      return target.getName();
   }

   private ClassLoader getCallerClassLoader() {
      return getClass().getClassLoader();
   }

   private static ClassLoader getClassLoader() {
      return Thread.currentThread().getContextClassLoader();
   }
}
