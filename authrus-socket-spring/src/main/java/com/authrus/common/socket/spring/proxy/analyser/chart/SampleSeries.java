package com.authrus.common.socket.spring.proxy.analyser.chart;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.authrus.common.chart.Series;

public class SampleSeries implements Series {

   private final LinkedList<Object> series;
   private final AtomicInteger capacity;

   public SampleSeries(int capacity) {
      this.capacity = new AtomicInteger(capacity);
      this.series = new LinkedList<Object>();
   }

   public synchronized int getCapacity() {
      return capacity.get();
   }

   public synchronized void setCapacity(int records) {
      capacity.set(records);
   }

   @Override
   public synchronized int getLength() {
      return series.size();
   }

   @Override
   public synchronized Object getValue(int index) {
      return series.get(index);
   }

   public synchronized void sample(Object sample) {
      if (series.isEmpty()) {
         series.addLast(sample);
      } else {
         int records = capacity.get();
         int size = series.size();

         while (size-- >= records) {
            series.removeFirst();
         }
         series.addLast(sample);
      }
   }
}
