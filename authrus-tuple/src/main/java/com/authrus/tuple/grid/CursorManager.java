package com.authrus.tuple.grid;

import java.util.HashMap;
import java.util.Map;

import com.authrus.predicate.Predicate;
import com.authrus.tuple.query.PredicateFilter;

public class CursorManager {

   private final Map<Grid, Cursor> cursors;
   private final CursorBuilder builder;
   private final Version start;
   private final PredicateFilter filter;

   public CursorManager(PredicateFilter filter) {
      this.cursors = new HashMap<Grid, Cursor>();
      this.builder = new CursorBuilder(filter);
      this.start = new Version(-1);
      this.filter = filter;
   }

   public Cursor createCursor(Grid grid, String type) {
      Cursor cursor = cursors.get(grid);

      if (cursor == null) {
         Predicate predicate = filter.getPredicate(type);

         if (predicate != null) {
            return new Cursor(predicate, start, start, start);
         }
         return null;
      }
      return cursor;
   }

   public void updateCursor(Grid grid, Delta delta, String type) {
      Predicate predicate = filter.getPredicate(type);

      if (predicate != null) {
         Cursor cursor = builder.createCursor(delta, type);

         if (cursor != null) {
            cursors.put(grid, cursor);
         }
      }
   }
}
