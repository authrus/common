package com.authrus.tuple.grid.validate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SelfValidatingMessage implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public String key;
   public int sumOfAll;
   public int value0;
   public int value1;
   public int value2;
   public int value3;
   public int value4;
   public int value5;
   public int value6;
   public int value7;
   public int value8;
   public int value9;
   public long time;
   
   public long elapsedTime(){
      return TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - time);
   }

   public int calculateSum() {
      return value0 + value1 + value2 + value3 + value4 + value5 + value6 + value7 + value8 + value9;
   }

   public boolean isValid() {
      return calculateSum() == sumOfAll;
   }
}
