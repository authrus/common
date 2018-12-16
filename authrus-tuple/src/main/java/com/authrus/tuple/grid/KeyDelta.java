package com.authrus.tuple.grid;

import java.util.List;

public class KeyDelta {

   private final List<Key> newKeys;
   private final Version version;

   public KeyDelta(List<Key> newKeys, Version version) {
      this.newKeys = newKeys;
      this.version = version;
   }

   public Version getVersion() {
      return version;
   }

   public List<Key> getChanges() {
      return newKeys;
   }

   @Override
   public String toString() {
      return newKeys.toString();
   }
}
