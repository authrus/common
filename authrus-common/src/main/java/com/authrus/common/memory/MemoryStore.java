package com.authrus.common.memory;

public interface MemoryStore<T> {
   boolean exists(String name);
   int sizeOf(T memory, String name);
   void save(T memory, String name);
   void save(T memory, String name, boolean recycle);
   boolean stale(T memory, String name);
   void dispose(T memory, String name);
   T read(String name);
   void clear();
}
