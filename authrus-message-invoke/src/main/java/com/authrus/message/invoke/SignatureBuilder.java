package com.authrus.message.invoke;

import java.lang.reflect.Method;

public class SignatureBuilder {

   public String createSignature(Class type, Method method) {
      StringBuilder builder = new StringBuilder();

      if (method != null) {
         Class[] parameters = method.getParameterTypes();
         String source = type.getName();
         String name = method.getName();

         builder.append(source);
         builder.append(".");
         builder.append(name);
         builder.append("(");

         if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
               String argument = parameters[i].getName();

               if (i > 0) {
                  builder.append(", ");
               }
               builder.append(argument);
            }
         }
         builder.append(")");
      }
      return builder.toString();
   }
}
