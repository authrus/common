package com.authrus.tuple.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.tuple.Tuple;

public class DeltaMergerTest extends TestCase {

   public void testDeltaMerger() throws Exception {
      DeltaMerger merger = new DeltaMerger();
      String[] index = new String[] { "a", "b" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);
      RowConverter converter = new RowConverter(structure, schema, "type");

      schema.getColumn("a");
      schema.getColumn("b");
      schema.getColumn("c");
      schema.getColumn("d");
      schema.getColumn("e");
      schema.getColumn("f");

      Map<String, Object> data = new HashMap<String, Object>();
      Tuple tuple = new Tuple(data, "type");
      
      data.put("a", "A");
      data.put("b", "B");
      data.put("c", "C");
      data.put("d", "D");
      data.put("e", "E");
      data.put("f", "F");

      Row row = converter.toRow(tuple);
      Cell[] cells = row.getCells();
      List<Cell> cellList = Arrays.asList(cells);

      Key key = new Key("key", null, 0, 0);
      RowDelta rowDelta = new RowDelta(schema, cellList, key, 0, 0, 0);
      Row mergedRow = merger.mergeRowDelta(rowDelta).getCurrent();

      assertEquals(mergedRow.getCell("a").getValue(), "A");
      assertEquals(mergedRow.getCell("b").getValue(), "B");
      assertEquals(mergedRow.getCell("c").getValue(), "C");
      assertEquals(mergedRow.getCell("d").getValue(), "D");
      assertEquals(mergedRow.getCell("e").getValue(), "E");
      assertEquals(mergedRow.getCell("f").getValue(), "F");

      List<Cell> updatedCells = new ArrayList<Cell>();

      updatedCells.add(new Cell("c", "newC", null));
      updatedCells.add(new Cell("f", "newF", null));
      updatedCells.add(new Cell("b", "newB", null));

      RowDelta nextRowDelta = new RowDelta(schema, updatedCells, key, 0, 1, 0);
      DeltaMerge deltaMerge = merger.mergeRowDelta(nextRowDelta);
      Row rowAfterMerge = deltaMerge.getCurrent();

      assertEquals(rowAfterMerge.getCell("a").getValue(), "A");
      assertEquals(rowAfterMerge.getCell("b").getValue(), "newB");
      assertEquals(rowAfterMerge.getCell("c").getValue(), "newC");
      assertEquals(rowAfterMerge.getCell("d").getValue(), "D");
      assertEquals(rowAfterMerge.getCell("e").getValue(), "E");
      assertEquals(rowAfterMerge.getCell("f").getValue(), "newF");
   }
}
