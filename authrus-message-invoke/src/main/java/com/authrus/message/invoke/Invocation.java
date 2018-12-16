package com.authrus.message.invoke;

import java.io.Serializable;

public class Invocation implements Serializable {

   private final Serializable[] arguments;
   private final String signature;
   private final Class type;

   public Invocation(Class type, String signature, Serializable[] arguments) {
      this.arguments = arguments;
      this.signature = signature;
      this.type = type;
   }

   public Class getType() {
      return type;
   }

   public Serializable[] getArguments() {
      return arguments;
   }

   public String getSignature() {
      return signature;
   }

   @Override
   public String toString() {
      return signature;
   }
}
