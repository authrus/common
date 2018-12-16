package com.authrus.common.collections;

import java.util.Comparator;

public class LeastFirstQueue<T extends Weight> extends WeightQueue<T> {

   public LeastFirstQueue() {
      this(-1);
   }

   public LeastFirstQueue(int capacity) {
      super(new LeastFirstComparator<T>(), capacity);
   }

   private static final class LeastFirstComparator<T extends Weight> implements Comparator<T> {

      @Override
      public int compare(T left, T right) {
         Long leftWeight = left.getWeight();
         Long rightWeight = right.getWeight();

         return leftWeight.compareTo(rightWeight);
      }
   }
}
