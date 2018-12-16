package com.authrus.tuple.grid;

import java.util.concurrent.atomic.AtomicLong;

public class Version implements Comparable<Version> {

   private final AtomicLong version;

   public Version() {
      this(0);
   }

   public Version(long version) {
      this.version = new AtomicLong(version);
   }

   public Version copy() {
      long value = version.get();

      if (value > 0) {
         return new Version(value);
      }
      return new Version(0);
   }

   public Version next() {
      long value = version.get();

      if (value > 0) {
         return new Version(value + 1);
      }
      return new Version(1);
   }

   public Version update() {
      long value = version.incrementAndGet();

      if (value > 0) {
         return new Version(value);
      }
      return new Version(0);
   }

   public long get() {
      return version.get();
   }   

   @Override
   public int compareTo(Version other) {
      Long left = version.get();
      Long right = other.version.get();
      
      return left.compareTo(right);
   }

   public boolean same(Version other) {
      long current = version.get();
      long value = other.get();

      return current == value;
   }

   public boolean after(Version other) {
      long current = version.get();
      long value = other.get();

      return current > value;
   }

   public boolean before(Version other) {
      long current = version.get();
      long value = other.get();

      return current < value;
   }

   public void set(long value) {
      version.set(value);
   }

   @Override
   public String toString() {
      return version.toString();
   }
}
