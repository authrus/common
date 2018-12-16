package com.authrus.attribute.transform;

public class IdentityTransform<T> implements ObjectTransform<T, T>{

   @Override
   public T toObject(T value) throws Exception {
      return value;
   }

   @Override
   public T fromObject(T object) throws Exception {
      return object;
   }

}
