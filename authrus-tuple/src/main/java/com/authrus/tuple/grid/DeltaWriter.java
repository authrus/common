package com.authrus.tuple.grid;

import java.util.List;

import com.authrus.io.DataFormatter;
import com.authrus.io.DataWriter;

public class DeltaWriter {

   private final DataFormatter formatter;

   public DeltaWriter(DataFormatter formatter) {
      this.formatter = formatter;
   }

   public void writeDelta(DataWriter writer, Delta delta) throws Exception {
      SchemaDelta schema = delta.getSchema();
      TableDelta table = delta.getTable();
      KeyDelta keys = table.getKeys();
      String type = delta.getType();

      writeType(writer, type);
      writeKeyDelta(writer, keys);
      writeSchemaDelta(writer, schema);
      writeTableDelta(writer, table);
   }

   private void writeType(DataWriter writer, String type) throws Exception {
      if (type == null) {
         writer.writeBoolean(false);
      } else {
         writer.writeBoolean(true);
         writer.writeString(type);
      }
   }

   private void writeKeyDelta(DataWriter writer, KeyDelta delta) throws Exception {
      List<Key> keys = delta.getChanges();
      Version version = delta.getVersion();
      int indexCount = keys.size();
      long value = version.get();

      writer.writeLong(value);
      writer.writeInt(indexCount);

      for (Key key : keys) {
         String name = key.getName();
         long revision = key.getRevision();
         int row = key.getIndex();

         writer.writeInt(row);
         writer.writeLong(revision);
         writer.writeString(name);
      }
   }

   private void writeTableDelta(DataWriter writer, TableDelta delta) throws Exception {
      List<RowDelta> changes = delta.getChanges();
      Version version = delta.getVersion();
      int rowCount = changes.size();
      long value = version.get();

      writer.writeLong(value);
      writer.writeInt(rowCount);

      for (RowDelta row : changes) {
         writeRowDelta(writer, row);
      }
   }

   private void writeRowDelta(DataWriter writer, RowDelta delta) throws Exception {
      List<Cell> changes = delta.getChanges();
      Schema schema = delta.getSchema();
      Key index = delta.getKey();
      long time = delta.getTime();
      long change = delta.getChange();
      int columns = delta.getColumns();
      int size = changes.size();
      int row = index.getIndex();

      writer.writeLong(time);
      writer.writeLong(change);
      writer.writeInt(row); // the key index
      writer.writeInt(columns); // this is not really needed
      writer.writeInt(size); // number of cells

      for (Cell cell : changes) {
         Object value = cell.getValue();
         String name = cell.getColumn();
         Column column = schema.getColumn(name);
         int position = column.getIndex();

         writer.writeInt(position);
         formatter.write(writer, value);
      }
   }

   private void writeSchemaDelta(DataWriter writer, SchemaDelta delta) throws Exception {
      List<Column> columns = delta.getColumns();
      Version version = delta.getVersion();
      int columnCount = columns.size();
      long value = version.get();

      writer.writeLong(value);
      writer.writeInt(columnCount);

      for (Column column : columns) {
         String name = column.getName();
         int index = column.getIndex();

         writer.writeInt(index);
         writer.writeString(name);
      }
   }
}
