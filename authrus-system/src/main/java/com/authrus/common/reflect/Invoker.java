package com.authrus.common.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.authrus.common.util.StringConverter;

public class Invoker {

   public static Object invoke(Object value, String methodName, String... parameters) throws Exception {
      return invoke(value, methodName, Arrays.asList(parameters));
   }

   public static Object invoke(Object value, String methodName, List<String> parameters) throws Exception{
      Class type = value.getClass();
      Method[] list = type.getDeclaredMethods();
      Object[] values = new Object[parameters.size()];
      StringConverter converter = new StringConverter();

      for(int i = 0; i < list.length; i++) {
         Method method = list[i];
         if(method.getName().equalsIgnoreCase(methodName)) {
            Class[] parameterTypes = method.getParameterTypes();
            int matchCount = 0;

            if(parameterTypes.length == parameters.size()) {
               for(int j = 0; j < parameterTypes.length; i++) {
                  Class parameterType = parameterTypes[j];

                  if(!converter.accept(parameterType)) {
                     break;
                  }
               }
            }
            if(matchCount == parameterTypes.length) {
               for(int j = 0; j < parameterTypes.length; i++) {
                  Class parameterType = parameterTypes[j];
                  String text = parameters.get(j);

                  if(text != null) {
                     values[j] = converter.convert(parameterType, String.valueOf(text));
                  }
               }
               return method.invoke(value, values);
            }
         }
      }
      throw new IllegalStateException("Could not find a method to match "+
            methodName + " in " +type + " with " + parameters.size() + " parameters");
   }
}
