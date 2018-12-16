package com.authrus.tuple.queue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ElementBatch implements Iterable<Element> {

   private final List<Element> list;

   public ElementBatch() {
      this(16);
   }
   
   public ElementBatch(int capacity) {      
      this.list = new ArrayList<Element>(capacity);
   }

   public boolean isEmpty() {
      return list.isEmpty();
   }

   public boolean remove(Element element) {
      return list.remove(element);
   }

   public void insert(Element element) {
      list.add(element);
   }

   public void insert(ElementBatch batch) {
      list.addAll(batch.list);
   }

   public Iterator<Element> iterator() {
      return list.iterator();
   }

   public int size() {
      return list.size();
   }

   public void clear() {
      list.clear();
   }
}
