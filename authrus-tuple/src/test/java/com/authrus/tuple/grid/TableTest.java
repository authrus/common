package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;
import com.authrus.tuple.Tuple;

public class TableTest extends TestCase {

   public void testExceedLimit() throws Exception {
      String[] index = new String[] { "key" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      Table table = new Table(structure, schema);
      RowConverter converter = new RowConverter(structure, schema, "type");

      for(int i = 0; i < 1000; i++) {
         Map<String, Object> data1 = new HashMap<String, Object>();
         Tuple tuple1 = new Tuple(data1, "type");
   
         data1.put("key", String.valueOf(i));
         data1.put("a", "A");
         data1.put("b", "B");
         data1.put("c", "C");
         data1.put("d", "D");
         data1.put("e", "E");
         data1.put("f", "F");
   
         Row row1 = converter.toRow(tuple1);
   
         table.insertRow(row1);
      }
   }
   
   public void testKeyDelta() throws Exception {
      String[] index = new String[] { "key" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      Table table = new Table(structure, schema);
      RowConverter converter = new RowConverter(structure, schema, "type");
      Predicate predicate = new Any();
      Version version = new Version(-1);

      Map<String, Object> data1 = new HashMap<String, Object>();
      Tuple tuple1 = new Tuple(data1, "type");

      data1.put("key", "blah");
      data1.put("a", "A");
      data1.put("b", "B");
      data1.put("c", "C");
      data1.put("d", "D");
      data1.put("e", "E");
      data1.put("f", "F");

      Row row1 = converter.toRow(tuple1);

      table.insertRow(row1);

      Row selectedRow1 = table.selectRow("blah");
      Version version1 = selectedRow1.getVersion();

      assertEquals(version1.get(), 1);

      Cursor cursor = new Cursor(predicate, version, version, version);

      TableDelta tableDelta1 = table.changeSince(cursor);
      KeyDelta keyDelta1 = tableDelta1.getKeys();
      Version tableVersion1 = tableDelta1.getVersion();
      Version keyVersion1 = keyDelta1.getVersion();

      assertEquals(tableVersion1.get(), 1); // one change only
      assertEquals(keyVersion1.get(), 1); // one new row only

      Map<String, Object> data2 = new HashMap<String, Object>(data1);
      Tuple tuple2 = new Tuple(data2, "type");
      
      data2.put("a", "A2");
      data2.put("b", "B2");

      Row row2 = converter.toRow(tuple2);

      table.insertRow(row2);

      Row selectedRow2 = table.selectRow("blah");
      Version version2 = selectedRow2.getVersion();

      assertEquals(version2.get(), 2);

      TableDelta tableDelta2 = table.changeSince(cursor);
      KeyDelta keyDelta2 = tableDelta2.getKeys();
      Version tableVersion2 = tableDelta2.getVersion();
      Version keyVersion2 = keyDelta2.getVersion();

      assertEquals(tableVersion2.get(), 2); // change was made to some row cells
      assertEquals(keyVersion2.get(), 1); // same version as a new row was not
                                          // added

   }

   public void testTable() throws Exception {
      String[] index = new String[] { "key" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      Table table = new Table(structure, schema);
      RowConverter converter = new RowConverter(structure, schema, "type");
      Predicate predicate = new Any();
      Version version = new Version();

      schema.getColumn("a");
      schema.getColumn("b");
      schema.getColumn("c");
      schema.getColumn("d");
      schema.getColumn("e");
      schema.getColumn("f");

      Map<String, Object> data1 = new HashMap<String, Object>();
      Tuple tuple1 = new Tuple(data1, "type");

      data1.put("key", "blah");
      data1.put("a", "A");
      data1.put("b", "B");
      data1.put("c", "C");
      data1.put("d", "D");
      data1.put("e", "E");
      data1.put("f", "F");

      Row row1 = converter.toRow(tuple1);

      table.insertRow(row1);

      Row selectedRow = table.selectRow("blah");

      assertEquals(selectedRow.getCell("key").getValue(), "blah");
      assertEquals(selectedRow.getCell("a").getValue(), "A");
      assertEquals(selectedRow.getCell("b").getValue(), "B");
      assertEquals(selectedRow.getCell("c").getValue(), "C");
      assertEquals(selectedRow.getCell("d").getValue(), "D");

      Version versionOfNewRow = row1.getVersion(); // current version of table
      Version nextVersion = versionOfNewRow.next();

      Cursor cursor = new Cursor(predicate, version, nextVersion, version);

      TableDelta delta = table.changeSince(cursor);

      assertNotNull(delta);
      assertTrue(delta.getChanges().isEmpty());

      Map<String, Object> data2 = new HashMap<String, Object>();
      Tuple tuple2 = new Tuple(data2, "type");
      
      data2.put("key", "someOtherKey");
      data2.put("a", "A");
      data2.put("b", "B");
      data2.put("c", "C");
      data2.put("d", "D");
      data2.put("e", "E");
      data2.put("f", "F");

      Row row2 = converter.toRow(tuple2);

      table.insertRow(row2); // a new row is added here

      Version currentTableVersion = delta.getVersion();
      Cursor cursorForNewRow = new Cursor(predicate, version, currentTableVersion, version);

      TableDelta deltaForNewRow = table.changeSince(cursorForNewRow);

      assertNotNull(deltaForNewRow);
      assertFalse(deltaForNewRow.getChanges().isEmpty());
      assertEquals(deltaForNewRow.getChanges().size(), 1);
      assertEquals(deltaForNewRow.getChanges().get(0).getKey().getName(), "someOtherKey");
      assertEquals(deltaForNewRow.getChanges().get(0).getKey().getIndex(), 1);

      List<String> newRowColumns = new ArrayList<String>();

      List<RowDelta> rowsChanged = deltaForNewRow.getChanges();
      RowDelta rowDelta = rowsChanged.get(0);
      List<Cell> changedCells = rowDelta.getChanges();

      for (Cell cell : changedCells) {
         newRowColumns.add(cell.getColumn());
      }
      assertEquals(newRowColumns.size(), 7);
      assertTrue(newRowColumns.contains("key"));
      assertTrue(newRowColumns.contains("a"));
      assertTrue(newRowColumns.contains("b"));
      assertTrue(newRowColumns.contains("c"));
      assertTrue(newRowColumns.contains("d"));
      assertTrue(newRowColumns.contains("e"));
      assertTrue(newRowColumns.contains("f"));
   }
}
