package com.authrus.tuple.grid;

import com.authrus.predicate.Predicate;
import com.authrus.tuple.query.PredicateFilter;

class CursorBuilder {

   private final PredicateFilter filter;

   public CursorBuilder(PredicateFilter filter) {
      this.filter = filter;
   }

   public Cursor createCursor(Delta delta, String type) {
      Predicate predicate = filter.getPredicate(type);
      TableDelta tableDelta = delta.getTable();
      KeyDelta keyDelta = tableDelta.getKeys();
      SchemaDelta schemaDelta = delta.getSchema();
      Version schemaVersion = schemaDelta.getVersion();
      Version tableVersion = tableDelta.getVersion();
      Version keyVersion = keyDelta.getVersion();

      return new Cursor(predicate, schemaVersion, tableVersion, keyVersion);

   }
}
