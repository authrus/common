package com.authrus.attribute.transform;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTransform implements ObjectTransform<AtomicInteger, Integer> {

   public AtomicInteger toObject(Integer value) {
      return new AtomicInteger(value);
   }

   public Integer fromObject(AtomicInteger value) {
      return value.get();
   }
}
