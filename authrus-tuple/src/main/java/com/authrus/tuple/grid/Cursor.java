package com.authrus.tuple.grid;

import com.authrus.predicate.Predicate;

public class Cursor {

   private final Version schemaVersion;
   private final Version tableVersion;
   private final Version keyVersion;
   private final Predicate predicate;

   public Cursor(Predicate predicate, Version schemaVersion, Version tableVersion, Version keyVersion) {
      this.schemaVersion = schemaVersion;
      this.tableVersion = tableVersion;
      this.keyVersion = keyVersion;
      this.predicate = predicate;
   }

   public Version getKeyVersion() {
      return keyVersion;
   }

   public Version getTableVersion() {
      return tableVersion;
   }

   public Version getSchemaVersion() {
      return schemaVersion;
   }

   public Predicate getPredicate() {
      return predicate;
   }
}
