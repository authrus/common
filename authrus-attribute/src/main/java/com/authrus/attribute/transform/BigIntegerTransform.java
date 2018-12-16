package com.authrus.attribute.transform;

import java.math.BigInteger;

public class BigIntegerTransform implements ObjectTransform<BigInteger, String> {

   public BigInteger toObject(String value) {
      return new BigInteger(value);
   }

   public String fromObject(BigInteger value) {
      return value.toString();
   }
}
