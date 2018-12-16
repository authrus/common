package com.authrus.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PropertyMapper {

   private final Map<String, Constructor> constructorCache;
   private final Map<String, Method> propertyCache;
   private final Map<String, Method> methodCache;
   private final Set<String> failureCache;

   public PropertyMapper() {
      this.constructorCache = new ConcurrentHashMap<String, Constructor>();
      this.propertyCache = new ConcurrentHashMap<String, Method>();
      this.methodCache = new ConcurrentHashMap<String, Method>();
      this.failureCache = new CopyOnWriteArraySet<String>();
   }

   public Method getProperty(Class type, String name) throws Exception {
      String key = createMethodKey(name, type);
      Method method = propertyCache.get(key);

      if(!propertyCache.containsKey(key) && !failureCache.contains(key)) {
         Class base = type;

         while(base != null) {
            method = getPropertyMatch(base, name);

            if(method != null) {
               propertyCache.put(key, method);
               return method;
            }
            base = base.getSuperclass();
         }
         failureCache.add(key);
      }
      return propertyCache.get(key);
   }

   public Method getMethodMatch(Class type, String name, Class... parameters) throws Exception {
      String key = createMethodKey(name, type, parameters);
      Method result = methodCache.get(key);

      if(result == null) {
         Class base = type;

         while(base != null) {
            Method[] list = base.getDeclaredMethods();

            for(Method method : list) {
               String methodName = method.getName();

               if(methodName.equals(name)) {
                  Class[] methodParameters = method.getParameterTypes();
                  boolean variableArguments = method.isVarArgs();
                  
                  if(isMatch(parameters, methodParameters, variableArguments)) {
                     method.setAccessible(true);
                     methodCache.put(key, method);
                     return method;
                  }
               }
            }
            base = base.getSuperclass();
         }
      }
      return result;
   }
   
   public Constructor getConstructorMatch(Class type, String name, Class... parameters) throws Exception {
      String key = createMethodKey(name, type, parameters);
      Constructor result = constructorCache.get(key);

      if(result == null) {     
         Constructor[] list = type.getDeclaredConstructors();

         for(Constructor constructor : list) {
            String constructorName = constructor.getName();

            if(constructorName.equals(name)) {
               Class[] methodParameters = constructor.getParameterTypes();
               boolean variableArguments = constructor.isVarArgs();
               
               if(isMatch(parameters, methodParameters, variableArguments)) {
                  constructor.setAccessible(true);
                  constructorCache.put(key, constructor);
                  return constructor;
               }
            }
         }
      }
      return result;
   }
   
   private boolean isMatch(Class[] argumentTypes, Class[] methodParameters, boolean variableArguments) {
      if(methodParameters.length == argumentTypes.length || variableArguments) {
         int matchesRemaining = argumentTypes.length;
   
         for(int i = 0; i < methodParameters.length; i++) {
            Class methodParamBox = getBoxClass(methodParameters[i]);
            Class parameterBox = getBoxClass(argumentTypes[i]);
   
            if(parameterBox == null) {
               if(!methodParameters[i].isPrimitive()) {
                  matchesRemaining--;
               } else {
                  break;
               }
            } else if(methodParamBox.isAssignableFrom(parameterBox)) {
               matchesRemaining--;
            } else {
               break;
            }
         }         
         if(matchesRemaining > 0) {
            int startIndex = methodParameters.length - 1;
            
            if(variableArguments) {
               Class lastType = methodParameters[startIndex];
               
               if(!lastType.isArray()) {
                  throw new IllegalStateException("Last parameter in varargs method is not an array");
               }
               Class componentType = lastType.getComponentType();
               Class componentTypeBox = getBoxClass(componentType);
               
               for(int i = startIndex; i < argumentTypes.length; i++) {
                  Class parameterBox = getBoxClass(argumentTypes[i]);
         
                  if(parameterBox == null) {
                     if(!componentType.isPrimitive()) { // primitive cannot be null
                        matchesRemaining--;
                     } else {
                        break;
                     }
                  } else if(componentTypeBox.isAssignableFrom(parameterBox)) {
                     matchesRemaining--;
                  } else {
                     break;
                  }
               } 
               return matchesRemaining <= 1;
            }
         }
         return matchesRemaining == 0;
      }
      return false;
   }

   private Method getPropertyMatch(Class type, String name) throws Exception {
      Method[] methods = type.getDeclaredMethods();
      PropertyType[] types = PropertyType.values();

      for(Method method : methods) {
         for(PropertyType matchType : types) {
            if(matchType.isMatch(method)) {
               String property = matchType.getProperty(method);

               if(property.equalsIgnoreCase(name)) {
                  Class[] parameters = method.getParameterTypes();

                  if(parameters.length == 0) {
                     return method;
                  }
               }
            }
         }
      }
      return null;
   }

   private Class getBoxClass(Class type) {
      if(type != null) {
         if(type.isPrimitive()) {
            if(type == int.class) {
               return Integer.class;
            }
            if(type == float.class) {
               return Float.class;
            }
            if(type == long.class) {
               return Long.class;
            }
            if(type == double.class) {
               return Double.class;
            }
            if(type == byte.class) {
               return Byte.class;
            }
            if(type == short.class) {
               return Short.class;
            }
            if(type == boolean.class) {
               return Boolean.class;
            }
         }
      }
      return type;
   }

   private String createMethodKey(String name, Class owningType, Class... parameters) {
      StringBuilder key = new StringBuilder();
      key.append(owningType.getName());
      key.append(".");
      key.append(name);
      key.append("(");
      
      for(Class param : parameters) {
         if(param != null) {
            String type = param.getName();
            key.append(type);
         } else {
            key.append(param);
         }      
      }
      key.append(")");
      return key.toString();
   }

   private static enum PropertyType {
      GET("get"),
      IS("is");

      private final String prefix;
      private final int size;

      private PropertyType(String prefix) {
         this.size = prefix.length();
         this.prefix = prefix;
      }

      public boolean isMatch(Method method) {
         String name = method.getName();

         if(name.startsWith(prefix)) {
            Class type = method.getReturnType();

            if(Object.class.isAssignableFrom(type)) {
               return true;
            }
         }
         return false;
      }

      public String getProperty(Method method) {
         String name = method.getName();

         if(name.startsWith(prefix)) {
            name = name.substring(size);
         }
         return getPropertyName(name);
      }
   }

   public static String getPropertyName(Method method) {
      PropertyType[] types = PropertyType.values();

      for(PropertyType matchType : types) {
         if(matchType.isMatch(method)) {
            return matchType.getProperty(method);
         }
      }
      return method.getName();
   }

   public static String getPropertyName(String name) {
      int length = name.length();

      if(length > 0) {
         char[] array = name.toCharArray();
         char first = array[0];

         if(!isAcronym(array)) {
            array[0] = toLowerCase(first);
         }
         return new String(array);
      }
      return name;
   }

   private static boolean isAcronym(char[] array) {
      if(array.length < 2) {
         return false;
      }
      if(!isUpperCase(array[0])) {
         return false;
      }
      return isUpperCase(array[1]);
   }

   private static char toLowerCase(char value) {
      return Character.toLowerCase(value);
   }

   private static boolean isUpperCase(char value) {
      return Character.isUpperCase(value);
   }
}
