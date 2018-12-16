package com.authrus.message.invoke;

import java.io.Serializable;

import com.authrus.message.invoke.ReturnStatus;

public class ReturnValue implements Serializable {

   private final ReturnStatus status;
   private final Object value;

   public ReturnValue(ReturnStatus status, Object value) {
      this.status = status;
      this.value = value;
   }

   public Object getValue() {
      return value;
   }

   public ReturnStatus getStatus() {
      return status;
   }

   @Override
   public String toString() {
      return String.format("return of %s", status);
   }
}
