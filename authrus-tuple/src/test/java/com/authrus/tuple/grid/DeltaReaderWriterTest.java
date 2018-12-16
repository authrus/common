package com.authrus.tuple.grid;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferReader;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataFormatter;
import com.authrus.io.DataReader;
import com.authrus.io.DataWriter;
import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;

public class DeltaReaderWriterTest extends TestCase {

   public void testReaderWriter() throws Exception {
      DataFormatter readerFormatter = new DataFormatter();
      DeltaReader reader = new DeltaReader(readerFormatter);
      Structure structure = new Structure(new String[]{"A"});
      ColumnAllocator builder = new ColumnAllocator(structure);
      DataFormatter writerFormatter = new DataFormatter();
      DeltaWriter writer = new DeltaWriter(writerFormatter);
      Version startVersion = new Version();
      Predicate any = new Any();

      builder.getColumn("A");
      builder.getColumn("B");
      builder.getColumn("C");
      builder.getColumn("D");

      ByteBufferBuilder buffer = new ByteBufferBuilder();
      DataWriter appender = new ByteBufferWriter(buffer);
      Cursor cursor = new Cursor(new Any(), startVersion, startVersion, startVersion);
      SchemaDelta schemaDelta = builder.changeSince(cursor);

      assertEquals(schemaDelta.getColumns().size(), 4);

      List<Key> indexes = new ArrayList<Key>();
      indexes.add(new Key("row-1", startVersion, 1, 0));
      indexes.add(new Key("row-2", startVersion, 2, 0));
      indexes.add(new Key("row-3", startVersion, 3, 0));

      List<RowDelta> rowDeltas = new ArrayList<RowDelta>();
      rowDeltas.add(new RowDelta(builder, Arrays.asList(new Cell("A", "a", startVersion), new Cell("C", "c", startVersion)), new Key("row-1", startVersion, 1, 0), 0, 0, 2));

      rowDeltas.add(new RowDelta(builder, Arrays.asList(new Cell("D", "d", startVersion), new Cell("A", "a", startVersion), new Cell("B", "b", startVersion)), new Key("row-2", startVersion, 2, 0), 0, 0, 3));

      KeyDelta indexDelta = new KeyDelta(indexes, startVersion);
      TableDelta tableDelta = new TableDelta(rowDeltas, indexDelta, startVersion);
      Delta delta = new Delta(schemaDelta, tableDelta, Object.class.getName());

      writer.writeDelta(appender, delta);

      ByteBuffer result = buffer.extract();
      DataReader in = new ByteBufferReader(result);
      Delta resultingDelta = reader.readDelta(in);

      assertEquals(resultingDelta.getType(), Object.class.getName());
      assertEquals(resultingDelta.getSchema().getColumns().size(), 4);
      assertEquals(resultingDelta.getSchema().getColumns().get(0).getName(), "A");
      assertEquals(resultingDelta.getSchema().getColumns().get(0).getIndex(), 0);
      assertEquals(resultingDelta.getSchema().getColumns().get(1).getName(), "B");
      assertEquals(resultingDelta.getSchema().getColumns().get(1).getIndex(), 1);
      assertEquals(resultingDelta.getSchema().getColumns().get(2).getName(), "C");
      assertEquals(resultingDelta.getSchema().getColumns().get(2).getIndex(), 2);
      assertEquals(resultingDelta.getSchema().getColumns().get(3).getName(), "D");
      assertEquals(resultingDelta.getSchema().getColumns().get(3).getIndex(), 3);

      assertEquals(resultingDelta.getTable().getChanges().size(), 2);
      assertEquals(resultingDelta.getTable().getChanges().get(0).getColumns(), 2);
      assertEquals(resultingDelta.getTable().getChanges().get(0).getChanges().size(), 2);
      assertEquals(resultingDelta.getTable().getChanges().get(0).getChanges().get(0).getColumn(), "A");
      assertEquals(resultingDelta.getTable().getChanges().get(0).getChanges().get(0).getValue(), "a");
      assertEquals(resultingDelta.getTable().getChanges().get(0).getChanges().get(1).getColumn(), "C");
      assertEquals(resultingDelta.getTable().getChanges().get(0).getChanges().get(1).getValue(), "c");

      assertEquals(resultingDelta.getTable().getChanges().get(1).getColumns(), 3);
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().size(), 3);
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(0).getColumn(), "D");
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(0).getValue(), "d");
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(1).getColumn(), "A");
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(1).getValue(), "a");
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(2).getColumn(), "B");
      assertEquals(resultingDelta.getTable().getChanges().get(1).getChanges().get(2).getValue(), "b");

      assertEquals(resultingDelta.getTable().getKeys().getChanges().size(), 3);
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(0).getName(), "row-1");
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(0).getIndex(), 1);
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(1).getName(), "row-2");
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(1).getIndex(), 2);
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(2).getName(), "row-3");
      assertEquals(resultingDelta.getTable().getKeys().getChanges().get(2).getIndex(), 3);

      List<RowDelta> secondRowDeltas = new ArrayList<RowDelta>();
      secondRowDeltas.add(new RowDelta(builder, Arrays.asList(new Cell("A", "new-a", startVersion), new Cell("B", "b", startVersion)), new Key("row-1", startVersion, 1, 0), 0, 0, 3));

      secondRowDeltas.add(new RowDelta(builder, Arrays.asList(new Cell("A", "a", startVersion), new Cell("B", "new-b", startVersion)), new Key("row-2", startVersion, 2, 0), 0, 0, 4));

      Version version = new Version(-1);
      Cursor nextCursor = new Cursor(any, version, version, version);
      SchemaDelta secondSchemaDelta = builder.changeSince(nextCursor);

      KeyDelta secondIndexDelta = new KeyDelta(Collections.EMPTY_LIST, startVersion);
      TableDelta secondTableDelta = new TableDelta(secondRowDeltas, secondIndexDelta, startVersion);
      Delta secondDelta = new Delta(secondSchemaDelta, secondTableDelta, Object.class.getName());

      ByteBufferBuilder secondBuffer = new ByteBufferBuilder();
      DataWriter secondData = new ByteBufferWriter(secondBuffer);

      writer.writeDelta(secondData, secondDelta);

      ByteBuffer secondResult = secondBuffer.extract();
      DataReader secondIn = new ByteBufferReader(secondResult);
      Delta secondResultingDelta = reader.readDelta(secondIn);

      assertEquals(secondResultingDelta.getType(), Object.class.getName());
      assertEquals(secondResultingDelta.getTable().getChanges().size(), 2);
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getColumns(), 3);
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getKey().getName(), "row-1");
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getKey().getIndex(), 1);
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getChanges().size(), 2);
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getChanges().get(0).getColumn(), "A");
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getChanges().get(0).getValue(), "new-a");
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getChanges().get(1).getColumn(), "B");
      assertEquals(secondResultingDelta.getTable().getChanges().get(0).getChanges().get(1).getValue(), "b");
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getColumns(), 4);
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getKey().getName(), "row-2");
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getKey().getIndex(), 2);
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getChanges().size(), 2);
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getChanges().get(0).getColumn(), "A");
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getChanges().get(0).getValue(), "a");
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getChanges().get(1).getColumn(), "B");
      assertEquals(secondResultingDelta.getTable().getChanges().get(1).getChanges().get(1).getValue(), "new-b");
   }
}
