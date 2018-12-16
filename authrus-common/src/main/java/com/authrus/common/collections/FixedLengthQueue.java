package com.authrus.common.collections;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedLengthQueue<T> extends AbstractQueue<T> {

   private final BlockingQueue<T> queue;
   private final int capacity;

   public FixedLengthQueue() {
      this(100);
   }

   public FixedLengthQueue(int capacity) {
      this.queue = new LinkedBlockingQueue<T>();
      this.capacity = capacity;
   }

   @Override
   public boolean offer(T e) {
      int size = queue.size();

      while(size >= capacity) {
         if(queue.poll() == null) {
            break;
         }
         size = queue.size();
      }
      return queue.offer(e);
   }

   @Override
   public T poll() {
      return queue.poll();
   }

   @Override
   public T peek() {
      return queue.peek();
   }

   @Override
   public Iterator<T> iterator() {
      return queue.iterator();
   }

   @Override
   public int size() {
      return queue.size();
   }
}
