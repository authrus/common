package com.authrus.tuple.grid;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class KeyAllocator {

   private final Indexer<Key> indexer;
   private final KeyBuilder builder;
   private final Version version;

   public KeyAllocator(Revision revision) {
      this(revision, 20000);
   }
   
   public KeyAllocator(Revision revision, int capacity) {
      this.builder = new KeyBuilder(revision);
      this.version = new Version();
      this.indexer = new RotateIndexer<Key>(builder, version, capacity);      
   }
   
   public boolean containsKey(String name) {
      return indexer.contains(name);
   }

   public Iterator<Key> getKeys() {
      return indexer.iterator();
   }

   public Key getKey(String name) {
      return indexer.index(name);
   }

   public KeyDelta changeSince(Version reference) {
      Version current = version.copy();
      Iterator<Key> keys = indexer.iterator();

      if (!indexer.isEmpty()) {
         List<Key> changes = new ArrayList<Key>();

         while (keys.hasNext()) {
            Key key = keys.next();
            Version created = key.getVersion();

            if (created.after(reference)) {
               changes.add(key);
            }
         }
         return new KeyDelta(changes, current);
      }
      return new KeyDelta(EMPTY_LIST, current);
   }
}
