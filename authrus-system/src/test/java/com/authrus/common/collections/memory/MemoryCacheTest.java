package com.authrus.common.collections.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.authrus.common.memory.MemoryCache;
import com.authrus.common.memory.MemoryStore;

import junit.framework.TestCase;

public class MemoryCacheTest extends TestCase {

   private static class ExampleResource {

      public final AtomicBoolean disposed;
      public final String name;
      public final int size;

      public ExampleResource(String name, int size) {
         this.disposed = new AtomicBoolean(false);
         this.name = name;
         this.size = size;
      }

      public ExampleResource copyOf() {
         return new ExampleResource(name, size);
      }

      public int size() {
         return size;
      }

      public boolean isDisposed() {
         return disposed.get();
      }

      public void dispose() {
         disposed.set(true);
      }
   }

   private static class ExampleMemoryStore implements MemoryStore<ExampleResource> {

      private final Map<String, ExampleResource> fileSystem;

      public ExampleMemoryStore() {
         this.fileSystem = new HashMap<String, ExampleResource>();
      }

      @Override
      public boolean exists(String name) {
         return fileSystem.containsKey(name);
      }

      @Override
      public int sizeOf(ExampleResource memory, String name) {
         return memory.size();
      }

      @Override
      public void save(ExampleResource memory, String name) {
         save(memory, name, false);
      }

      @Override
      public void save(ExampleResource memory, String name, boolean recycle) {
         if(memory != null) {
            ExampleResource copy = memory.copyOf();
            fileSystem.put(name, copy);
         }
      }

      @Override
      public boolean stale(ExampleResource memory, String name) {
         return memory.isDisposed();
      }

      @Override
      public void dispose(ExampleResource memory, String name) {
         fileSystem.remove(name);
      }

      @Override
      public ExampleResource read(String name) {
         ExampleResource resource = fileSystem.get(name);

         if(resource != null) {
            return resource.copyOf();
         }
         return null;
      }

      @Override
      public String toString() {
         return fileSystem.keySet().toString();
      }

      @Override
      public void clear() {}
   }

   public void testMemoryCache() {
      ExampleMemoryStore memoryStore = new ExampleMemoryStore();
      MemoryCache<ExampleResource> memoryCache = new MemoryCache<ExampleResource>(memoryStore);

      memoryCache.put("1", new ExampleResource("1", 10));
      memoryCache.put("2", new ExampleResource("2", 10));
      memoryCache.put("3", new ExampleResource("3", 10));
      memoryCache.put("4", new ExampleResource("4", 10));
      memoryCache.put("5", new ExampleResource("5", 10));

      assertEquals(memoryCache.usedMemory(), 50);

      memoryCache.put("6", new ExampleResource("6", 10));
      memoryCache.put("7", new ExampleResource("7", 10));

      assertEquals(memoryCache.usedMemory(), 70);

      memoryCache.put("1", new ExampleResource("1", 10));
      memoryCache.put("2", new ExampleResource("2", 10));

      assertEquals(memoryCache.usedMemory(), 70);

      assertFalse(memoryStore.exists("1"));
      assertFalse(memoryStore.exists("2"));
      assertFalse(memoryStore.exists("3"));
      assertFalse(memoryStore.exists("4"));
      assertFalse(memoryStore.exists("5"));
      assertFalse(memoryStore.exists("6"));
      assertFalse(memoryStore.exists("7"));

      memoryCache.put("8", new ExampleResource("8", 10));
      memoryCache.put("9", new ExampleResource("9", 10));

      assertEquals(memoryCache.usedMemory(), 90);

      memoryCache.put("10", new ExampleResource("10", 10));

      assertEquals(memoryCache.usedMemory(), 90);

      assertTrue(memoryStore.exists("1")); // overflow has occured because (capacity - maximum) was reached
      assertFalse(memoryStore.exists("2"));
      assertFalse(memoryStore.exists("3"));
      assertFalse(memoryStore.exists("4"));
      assertFalse(memoryStore.exists("5"));
      assertFalse(memoryStore.exists("6"));
      assertFalse(memoryStore.exists("7"));
      assertFalse(memoryStore.exists("8"));
      assertFalse(memoryStore.exists("9"));
      assertFalse(memoryStore.exists("10"));

      assertTrue(memoryCache.contains("1"));
      assertEquals(memoryCache.get("1").name, "1"); // loads it back in to the cache
      assertEquals(memoryCache.get("2").name, "2"); // loads it back in to the cache
      assertEquals(memoryCache.get("3").name, "3"); // loads it back in to the cache

      System.err.println(memoryStore);

      assertFalse(memoryStore.exists("5"));
      assertFalse(memoryStore.exists("6"));

      ExampleResource resource4 = memoryCache.get("4");
      ExampleResource resource5 = memoryCache.get("5");

      resource4.dispose();
      resource5.dispose();

      assertTrue(resource4.isDisposed());
      assertTrue(resource5.isDisposed());

      assertNotNull(memoryCache.get("4"));
      assertNotNull(memoryCache.get("5"));
      assertFalse(memoryCache.get("4").isDisposed()); // cache will reload any stale value
      assertFalse(memoryCache.get("5").isDisposed());

      assertEquals(memoryCache.usedMemory(), 90);

   }
}
