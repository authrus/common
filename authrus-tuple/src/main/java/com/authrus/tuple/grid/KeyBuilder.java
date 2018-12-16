package com.authrus.tuple.grid;

class KeyBuilder implements IndexBuilder<Key> {
   
   private final Revision revision;
   
   public KeyBuilder(Revision revision) {
      this.revision = revision;
   }

   @Override
   public Key createIndex(String name, Version version, int index) {
      Version current = revision.getCurrent();
      long revision = current.get();
      
      return new Key(name, version, index, revision);
   }
}
