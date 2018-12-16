package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.authrus.tuple.grid.Index;
import com.authrus.tuple.grid.IndexBuilder;
import com.authrus.tuple.grid.RotateIndexer;
import com.authrus.tuple.grid.Version;

public class IndexAllocatorTest extends TestCase {

   public void testIndexAllocatorSimpleCount() throws Exception {
      Version version = new Version();
      ExampleIndexBuilder builder = new ExampleIndexBuilder();
      RotateIndexer<ExampleIndex> allocator = new RotateIndexer<ExampleIndex>(builder, version, 20);

      for(int i = 0; i < 100; i++) {
         ExampleIndex newIndex = allocator.index("index-[" + i + "]");
         Iterator<ExampleIndex> iterator = allocator.iterator();
         StringBuilder text = new StringBuilder();
         String separator = "";
         boolean found = false;
         int count = 0;
         while(iterator.hasNext()) {
            ExampleIndex index = iterator.next();
            if(index != null) {
               count++;
               text.append(separator);
               text.append(index.index);
               if(index.name.equals(newIndex.name)) {
                  found = true;
                  text.append("*");
               }
               separator = ",";
            }
         }
         assertTrue("Could not find key " + i, found);
         Thread.sleep(10);
         System.err.println(newIndex.name + " -> " + newIndex.index + " [" + text + "] = " + count);
      }
   }

   public void testMultiThreadedIndexAllocator() throws Exception {
      final Version version = new Version();
      final ExampleIndexBuilder builder = new ExampleIndexBuilder();
      final RotateIndexer<ExampleIndex> allocator = new RotateIndexer<ExampleIndex>(builder, version, 50);
      final List<Thread> list = new ArrayList<Thread>();

      for(int i = 0; i < 10; i++) {
         final String name = "thread-" + i;
         Thread thread = new Thread(new Runnable() {
            public void run() {
               try {
                  for(int i = 0; i < 1000; i++) {
                     ExampleIndex newIndex = allocator.index(name + "-[" + i + "]");                     
                     Iterator<ExampleIndex> iterator = allocator.iterator();
                     StringBuilder text = new StringBuilder();
                     String separator = "";
                     boolean found = false;
                     int count = 0;
                     while(iterator.hasNext()) {
                        ExampleIndex index = iterator.next();
                        if(index != null) {
                           count++;
                           text.append(separator);
                           text.append(index.index);
                           if(index.name.equals(newIndex.name)) {
                              found = true;
                              text.append("*");
                           }
                           separator = ",";
                        }
                     }
                     assertTrue("Could not find key " + i, found);
                     Thread.sleep(10);
                     System.err.println(newIndex.name + " -> " + newIndex.index + " [" + text + "] = " + count);
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               }
            }
         });
         thread.start();
         list.add(thread);
      }
      for(Thread thread : list) {
         thread.join();
      }
   }

   public void testIndexAllocator() throws Exception {
      Version version = new Version();
      ExampleIndexBuilder builder = new ExampleIndexBuilder();
      RotateIndexer<ExampleIndex> allocator = new RotateIndexer<ExampleIndex>(builder, version, 20);

      for(int i = 0; i < 1000; i++) {
         ExampleIndex newIndex = allocator.index("index-[" + i + "]");         
         Iterator<ExampleIndex> iterator = allocator.iterator();
         StringBuilder text = new StringBuilder();
         String separator = "";
         boolean found = false;
         int count = 0;
         while(iterator.hasNext()) {
            ExampleIndex index = iterator.next();
            if(index != null) {
               text.append(separator);
               text.append(index.index);
               count++;
               if(index.name.equals(newIndex.name)) {
                  found = true;
                  text.append("*");
               }
               separator = ",";
            }
         }
         assertTrue("Could not find key " + i, found);
         Thread.sleep(10);
         System.err.println(newIndex.name + " -> " + newIndex.index + " [" + text + "] = " + count);
      }
   }

   public static class ExampleIndex implements Index {

      public final int index;
      public final Version version;
      public final String name;

      public ExampleIndex(String name, Version version, int index) {
         this.index = index;
         this.name = name;
         this.version = version;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public int getIndex() {
         return index;
      }

      public String toString() {
         return name + " --> " + index;
      }
   }

   public static class ExampleIndexBuilder implements IndexBuilder<ExampleIndex> {

      @Override
      public ExampleIndex createIndex(String name, Version version, int index) {
         return new ExampleIndex(name, version, index);
      }

   }

}
