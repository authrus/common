package com.authrus.common.collections;

import java.util.Comparator;

public class GreatestFirstQueue<T extends Weight> extends WeightQueue<T> {

   public GreatestFirstQueue() {
      this(-1);
   }

   public GreatestFirstQueue(int capacity) {
      super(new GreatestFirstComparator<T>(), capacity);
   }

   private static final class GreatestFirstComparator<T extends Weight> implements Comparator<T> {

      @Override
      public int compare(T left, T right) {
         Long leftWeight = left.getWeight();
         Long rightWeight = right.getWeight();

         return -leftWeight.compareTo(rightWeight);
      }
   }
}
