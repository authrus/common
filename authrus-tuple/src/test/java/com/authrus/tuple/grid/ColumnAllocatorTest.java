package com.authrus.tuple.grid;

import junit.framework.TestCase;

import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;
import com.authrus.tuple.grid.Cursor;
import com.authrus.tuple.grid.SchemaDelta;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.Version;

public class ColumnAllocatorTest extends TestCase {

   public void testVersionUpdate() throws Exception {
      Structure structure = new Structure(new String[]{"first"});
      ColumnAllocator allocator = new ColumnAllocator(structure, 100);
      Version initialVersion = new Version(-1);
      Predicate predicate = new Any();
      Cursor cursor = new Cursor(predicate, initialVersion, initialVersion, initialVersion);

      SchemaDelta delta1 = allocator.changeSince(cursor);
      Version schemaVersion1 = delta1.getVersion();
      long version1 = schemaVersion1.get();

      assertEquals(version1, 0);

      allocator.getColumn("A");
      allocator.getColumn("B");
      allocator.getColumn("C");

      assertNotNull(allocator.getColumn("A"));
      assertNotNull(allocator.getColumn("B"));
      assertNotNull(allocator.getColumn("C"));

      assertEquals(allocator.getColumn("A").getIndex(), 0);
      assertEquals(allocator.getColumn("B").getIndex(), 1);
      assertEquals(allocator.getColumn("C").getIndex(), 2);

      assertEquals(allocator.getCount(), 3);

      SchemaDelta delta2 = allocator.changeSince(cursor);
      Version schemaVersion2 = delta2.getVersion();
      long version2 = schemaVersion2.get();

      assertEquals(version2, 3);

      allocator.getColumn("C");

      SchemaDelta delta3 = allocator.changeSince(cursor);
      Version schemaVersion3 = delta3.getVersion();
      long version3 = schemaVersion3.get();

      assertEquals(version3, 3);

      allocator.getColumn("D");

      SchemaDelta delta4 = allocator.changeSince(cursor);
      Version schemaVersion4 = delta4.getVersion();
      long version4 = schemaVersion4.get();

      assertEquals(version4, 4);
   }

   public void testSchema() throws Exception {
      Structure structure = new Structure(new String[]{"first"});
      ColumnAllocator schema = new ColumnAllocator(structure, 100);

      schema.getColumn("first");
      schema.getColumn("second");
      schema.getColumn("third");

      assertNotNull(schema.getColumn("first"));
      assertEquals(schema.getColumn("first").getIndex(), 0);
      assertNotNull(schema.getColumn("second"));
      assertEquals(schema.getColumn("second").getIndex(), 1);
      assertNotNull(schema.getColumn("third"));
      assertEquals(schema.getColumn("third").getIndex(), 2);
   }
}
