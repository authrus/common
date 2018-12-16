package com.authrus.attribute.transform;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTransform implements ObjectTransform<AtomicLong, Long> {

   public AtomicLong toObject(Long value) {
      return new AtomicLong(value);
   }

   public Long fromObject(AtomicLong value) {
      return value.get();
   }
}
