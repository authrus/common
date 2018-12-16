package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.authrus.io.DataFormatter;
import com.authrus.io.DataReader;

public class DeltaReader {

   private Map<Integer, Key> indexes;
   private DataFormatter formatter;
   private ColumnBuilder builder;

   public DeltaReader(DataFormatter formatter) {
      this.indexes = new HashMap<Integer, Key>();
      this.builder = new ColumnBuilder();
      this.formatter = formatter;
   }

   public Delta readDelta(DataReader reader) throws Exception {
      String type = readType(reader);
      
      if(type == null) {
         throw new IllegalStateException("No type information found");
      }
      return readDelta(reader, type);
   }  
   
   public Delta readDelta(DataReader reader, String type) throws Exception {
      KeyDelta keys = readKeyDelta(reader);
      SchemaDelta schema = readSchemaDelta(reader);
      TableDelta table = readTableDelta(reader, keys);

      return new Delta(schema, table, type);   
   }

   public String readType(DataReader reader) throws Exception {
      if (reader.readBoolean()) {
         return reader.readString();
      }
      return null;
   }

   private Version readVersion(DataReader reader) throws Exception {
      long version = reader.readLong();

      if (version >= 0) {
         return new Version(version);
      }
      return new Version();
   }

   private TableDelta readTableDelta(DataReader reader, KeyDelta update) throws Exception {
      Version version = readVersion(reader);
      List<RowDelta> changes = readRowDeltas(reader, update, version);

      return new TableDelta(changes, update, version);
   }

   private List<RowDelta> readRowDeltas(DataReader reader, KeyDelta update, Version version) throws Exception {
      int count = reader.readInt();

      if (count > 0) {
         List<RowDelta> changes = new ArrayList<RowDelta>(count);
         
         for (int i = 0; i < count; i++) {
            RowDelta delta = readRowDelta(reader, update, version);
            changes.add(delta);
         }
         return changes;
      }
      return Collections.emptyList();
   }

   private RowDelta readRowDelta(DataReader reader, KeyDelta update, Version version) throws Exception {
      long time = reader.readLong();
      long change = reader.readLong();
      int row = reader.readInt();
      int columns = reader.readInt();
      Key key = indexes.get(row);

      if (key == null) {         
         throw new IllegalStateException("Row index " + row + " not found in " + indexes + " or update " + update);
      }
      List<Cell> rowCells = readRowCells(reader, version);

      if (rowCells == null) {
         throw new IllegalStateException("Row for key " + key + " had no changes");
      }
      return new RowDelta(builder, rowCells, key, time, change, columns);
   }

   private List<Cell> readRowCells(DataReader reader, Version version) throws Exception {
      int count = reader.readInt();

      if (count > 0) {
         List<Cell> changes = new ArrayList<Cell>(count);

         for (int i = 0; i < count; i++) {
            Cell cell = readCell(reader, version);
            changes.add(cell);
         }
         return changes;
      }
      return Collections.emptyList();
   }

   private Cell readCell(DataReader reader, Version version) throws Exception {
      int index = reader.readInt();
      Column column = builder.getColumn(index);

      if (column == null) {
         throw new IllegalStateException("Column at index " + index + " has no mapping");
      }
      String name = column.getName();
      Object value = formatter.read(reader);

      return new Cell(name, value, version);
   }

   private KeyDelta readKeyDelta(DataReader reader) throws Exception {
      Version version = readVersion(reader);
      List<Key> changes = readKeys(reader, version);

      return new KeyDelta(changes, version);
   }

   private List<Key> readKeys(DataReader reader, Version version) throws Exception {
      int count = reader.readInt();

      if (count > 0) {
         List<Key> list = new ArrayList<Key>(count);

         for (int i = 0; i < count; i++) {
            Key key = readKey(reader, version);
            
            if(key != null) {
               int index = key.getIndex();
            
               indexes.put(index, key);
               list.add(key);
            }
         }
         return list;
      }
      return Collections.emptyList();
   }

   private Key readKey(DataReader reader, Version version) throws Exception {
      int row = reader.readInt();
      long revision = reader.readLong();
      String name = reader.readString();

      return new Key(name, version, row, revision);
   }

   private SchemaDelta readSchemaDelta(DataReader reader) throws Exception {
      Version version = readVersion(reader);
      List<Column> columns = readColumns(reader, version);

      return new SchemaDelta(columns, version);
   }

   private List<Column> readColumns(DataReader reader, Version version) throws Exception {
      int count = reader.readInt();

      if (count > 0) {
         List<Column> columns = new ArrayList<Column>(count);

         for (int i = 0; i < count; i++) {
            Column column = readColumn(reader, version);
            columns.add(column);
         }
         return columns;
      }
      return Collections.emptyList();
   }

   private Column readColumn(DataReader reader, Version version) throws Exception {
      int index = reader.readInt();
      String name = reader.readString();

      return builder.addColumn(name, version, index);
   }

   private static class ColumnBuilder implements Schema {

      private Map<String, Column> names;
      private Column[] positions;
      
      public ColumnBuilder() {
         this.names = new HashMap<String, Column>();
         this.positions = new Column[0];
      }

      @Override
      public int getCount() {
         return names.size();
      }

      @Override
      public Column getColumn(int index) {
         if(index < positions.length) {
            return positions[index];
         }
         return null;
      }

      @Override
      public Column getColumn(String name) {
         return names.get(name);
      }

      public Column addColumn(String name, Version version, int index) {
         Column existing = names.get(name);

         if (existing != null) {
            int position = existing.getIndex();

            if (position != index) {
               throw new IllegalStateException("Attempting to replace index for '" + name + "'");
            }
         } else {
            Column column = new Column(name, version, index);

            if (index >= positions.length) {
               positions = Arrays.copyOf(positions, index + 1);
            }
            positions[index] = column;
            names.put(name, column);          
         }
         return names.get(name);
      }
   }
}
