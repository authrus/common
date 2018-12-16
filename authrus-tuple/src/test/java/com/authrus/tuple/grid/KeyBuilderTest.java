package com.authrus.tuple.grid;

import com.authrus.tuple.grid.Cell;
import com.authrus.tuple.grid.CellCombiner;
import com.authrus.tuple.grid.ColumnAllocator;
import com.authrus.tuple.grid.Row;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.Version;

import junit.framework.TestCase;

public class KeyBuilderTest extends TestCase {

   public void testGridKey() {
      Version version = new Version();
      String[] index = new String[] { "first", "third" };
      Structure structure = new Structure(index);
      ColumnAllocator schema = new ColumnAllocator(structure);

      schema.getColumn("first");
      schema.getColumn("second");
      schema.getColumn("third");
      schema.getColumn("fourth");
      schema.getColumn("fifth");

      Cell[] row = new Cell[6];

      row[0] = new Cell("first", "1", version);
      row[1] = new Cell("second", "2", version);
      row[2] = new Cell("third", "3", version);
      row[3] = new Cell("fourth", "4", version);
      row[4] = new Cell("fifth", "5", version);

      CellCombiner builder = new CellCombiner(schema);

      String key = builder.combineCells(row, index);

      Row gridRow = new Row(schema, version, row, key, 0, 0);

      assertNotNull(gridRow.getCell("first"));
      assertEquals(gridRow.getCell("first").getValue(), "1");
      assertNotNull(gridRow.getCell("second"));
      assertEquals(gridRow.getCell("second").getValue(), "2");
      assertNotNull(gridRow.getCell("third"));
      assertEquals(gridRow.getCell("third").getValue(), "3");
      assertNotNull(gridRow.getCell("fourth"));
      assertEquals(gridRow.getCell("fourth").getValue(), "4");
      assertNotNull(gridRow.getCell("fifth"));
      assertEquals(gridRow.getCell("fifth").getValue(), "5");

      Cell[] otherRow = new Cell[3];

      otherRow[0] = new Cell("first", "1", version);
      otherRow[1] = new Cell("second", "DIFFERENT", version);
      otherRow[2] = new Cell("third", "3", version);

      String otherKey = builder.combineCells(otherRow, index);

      Row otherGridRow = new Row(schema, version, otherRow, key, 0, 0);

      assertNotNull(otherGridRow.getCell("first"));
      assertEquals(otherGridRow.getCell("first").getValue(), "1");
      assertNotNull(otherGridRow.getCell("second"));
      assertEquals(otherGridRow.getCell("second").getValue(), "DIFFERENT");
      assertNotNull(otherGridRow.getCell("third"));
      assertEquals(otherGridRow.getCell("third").getValue(), "3");

      assertEquals(key.hashCode(), otherKey.hashCode());
      assertEquals(otherKey, key);

   }

}
