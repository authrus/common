package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.authrus.predicate.Predicate;
import com.authrus.tuple.Tuple;

/**
 * A grid contains objects mapped to a key value pair schema where each pair
 * represents a cell for a {@link Column} and the object acts as the {@link Row}.
 * It can contain only objects of one type, and must have a key value pair or
 * a group of pairs that represent the key.
 * <p>
 * A constraint can be added to each grid specify the columns that are constant,
 * which means the value represented by a {@link Cell} of that column must not
 * change. Establishing such a constraint ensures that {@link Predicate} objects
 * can filter rows from the grid, as only the constants can be evaluated by
 * predicates.
 * 
 * @author Niall Gallagher
 */
public class Grid {

   private final ColumnAllocator allocator;
   private final ChangeListener listener;
   private final RowConverter converter;
   private final Table table;
   private final String type;
   
   public Grid(ChangeListener listener, Structure structure, String type) {
      this(listener, structure, type, 20000);
   }
   
   public Grid(ChangeListener listener, Structure structure, String type, int capacity) {
      this.allocator = new ColumnAllocator(structure);
      this.converter = new RowConverter(structure, allocator, type);
      this.table = new Table(structure, allocator, capacity);
      this.listener = listener;
      this.type = type;
   }

   public boolean contains(String key) {
      return table.containsRow(key);
   }

   public Tuple find(String key) {
      Row row = table.selectRow(key);
      
      if(row != null) {
         return converter.fromRow(row);
      }
      return null;
   }
   
   public List<Tuple> find(Predicate predicate) {
      List<Row> rows = table.selectRows(predicate);
      
      if(!rows.isEmpty()) {
         List<Tuple> list = new ArrayList<Tuple>();
         
         for(Row row : rows) {
            Tuple tuple = converter.fromRow(row);
            
            if(tuple != null) {
               list.add(tuple);
            }
         }
         return list;
      }
      return Collections.emptyList();
   }     

   public Tuple update(Tuple tuple) {
      Row row = converter.toRow(tuple);
      Row update = table.insertRow(row);

      if (listener != null) {
         listener.onChange(this, allocator, type);
      }
      Map<String, Object> attributes = tuple.getAttributes();
      long change = update.getChange();
      long expect = tuple.getChange();
      
      if(expect == change  || expect == 0) {
         return new Tuple(attributes, type, change);
      }
      return null;
   }  

   public Delta change(Cursor cursor) {
      TableDelta tableDelta = table.changeSince(cursor); 
      SchemaDelta schemaDelta = allocator.changeSince(cursor);

      return new Delta(schemaDelta, tableDelta, type);
   }   
   
   public int size() {
      return table.countRows();
   }
}

