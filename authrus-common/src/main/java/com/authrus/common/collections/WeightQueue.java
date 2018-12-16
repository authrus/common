package com.authrus.common.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public abstract class WeightQueue<T extends Weight> implements Iterable<T> {

   private final PriorityQueue<T> queue;
   private final int limit;

   protected WeightQueue(Comparator<T> comparator) {
      this(comparator, -1);
   }

   protected WeightQueue(Comparator<T> comparator, int limit) {
      this.queue = new PriorityQueue<T>(100, comparator);
      this.limit = limit;
   }

   public Iterator<T> iterator() {
      return queue.iterator();
   }

   public void offer(T item) {
      if (queue.offer(item)) {
         int size = queue.size();

         if (size > limit && limit > 0) {
            queue.poll();
         }
      }
   }

   public T peek() {
      return queue.peek();
   }

   public T poll() {
      return queue.poll();
   }

   public int size() {
      return queue.size();
   }

   public boolean isEmpty() {
      return queue.isEmpty();
   }

   public void clear() {
      queue.clear();
   }
   
   @Override
   public String toString() {
      return queue.toString();
   }
}
