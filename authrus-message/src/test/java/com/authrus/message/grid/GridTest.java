package com.authrus.message.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.authrus.message.Message;
import com.authrus.message.MessagePublisher;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.message.bind.ObjectMarshaller;
import com.authrus.message.tuple.TupleMessagePublisher;
import com.authrus.predicate.Any;
import com.authrus.predicate.Predicate;
import com.authrus.predicate.PredicateParser;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.Cell;
import com.authrus.tuple.grid.Cursor;
import com.authrus.tuple.grid.Delta;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.TableDelta;
import com.authrus.tuple.grid.Version;

public class GridTest extends TestCase {

   private static final int INPUT_ITERATIONS = 1000000;
   
   public void testSelectPerformance() throws Exception {
      String[] index = new String[] { "name" };
      String[] constants = new String[] { "dateOfBirth", "placeOfBirth", "country" };
      Structure structure = new Structure(index);      
      ExampleObjectMarshaller marshaller = new ExampleObjectMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
      marshallers.put(ExampleObject.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      Grid grid = new Grid(null, structure, ExampleObject.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(ExampleObject.class.getName(), grid);
      
      ExampleObject object = new ExampleObject("tom", "19/11/1987", "sydney", "australia", "trader", "some place");
      Message message = new Message(object);

      adapter.publish(message);
      
      Version startVersion = new Version(); 
      Predicate predicate = new PredicateParser("country == 'australia0' || country == 'australia19'");
      Cursor firstCursor = new Cursor(predicate, startVersion, startVersion, startVersion);
      ExampleObject[] inputs = new ExampleObject[INPUT_ITERATIONS];
      
      for(int i = 0; i < INPUT_ITERATIONS; i++) {
         inputs[i] = new ExampleObject("name-" + (i % 1000), "19/11/1967", "sydney" + i, "australia" + i, "trader", "some place");
      }      
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < INPUT_ITERATIONS; i++) {
         Message newMessage = new Message(inputs[i]);

         adapter.publish(newMessage);
      }
      long endTime = System.currentTimeMillis();

      System.err.println("Time taken to update " + INPUT_ITERATIONS + " objects was " + (endTime - startTime) + " milliseconds");
      
      startTime = System.currentTimeMillis();

      for (int i = 0; i < 10000; i++) {
         grid.find(predicate);
      }
      endTime = System.currentTimeMillis();

      System.err.println("Time taken to select " + 10000 + " objects was " + (endTime - startTime) + " milliseconds");
   }
   

   public void testGridInputRate() throws Exception {
      String[] index = new String[] { "name" };
      Structure structure = new Structure(index);      
      ExampleObjectMarshaller marshaller = new ExampleObjectMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
      marshallers.put(ExampleObject.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      Grid grid = new Grid(null, structure, ExampleObject.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(ExampleObject.class.getName(), grid);
      
      ExampleObject object = new ExampleObject("tom", "19/11/1987", "sydney", "australia", "trader", "some place");
      Version startVersion = new Version();
      Predicate predicate = new Any();
      Cursor firstCursor = new Cursor(predicate, startVersion, startVersion, startVersion);
      Message message = new Message(object);

      adapter.publish(message);

      Delta deltaAfterUpdate = grid.change(firstCursor);
      TableDelta tableDelta = deltaAfterUpdate.getTable();
      Version afterFirstUpdate = tableDelta.getVersion();

      Tuple recovered = grid.find("tom");
      Map<String, Object> recoveredAttributes = recovered.getAttributes();      
      ExampleObject recoveredObject = (ExampleObject) binder.toObject(recoveredAttributes, ExampleObject.class.getName());

      assertNotNull(recoveredObject);
      assertEquals(recoveredObject.name, "tom");
      assertEquals(recoveredObject.dateOfBirth, "19/11/1987");
      assertEquals(recoveredObject.placeOfBirth, "sydney");
      assertEquals(recoveredObject.country, "australia");
      assertEquals(recoveredObject.job, "trader");
      assertEquals(recoveredObject.address, "some place");

      ExampleObject updateForTom = new ExampleObject("tom", "19/11/1967", "sydney", "australia", "trader", "some place");
      Message messageForTom = new Message(updateForTom);

      adapter.publish(messageForTom);

      Tuple updatedRecordForTom = grid.find("tom");
      Map<String, Object> updatedRecordForTomAttributes = updatedRecordForTom.getAttributes();      
      ExampleObject updatedRecordForTomObject = (ExampleObject) binder.toObject(updatedRecordForTomAttributes, ExampleObject.class.getName());

      assertNotNull(updatedRecordForTomObject);
      assertEquals(updatedRecordForTomObject.name, "tom");
      assertEquals(updatedRecordForTomObject.dateOfBirth, "19/11/1967");
      assertEquals(updatedRecordForTomObject.placeOfBirth, "sydney");
      assertEquals(updatedRecordForTomObject.country, "australia");
      assertEquals(updatedRecordForTomObject.job, "trader");
      assertEquals(updatedRecordForTomObject.address, "some place");

      Cursor cursor = new Cursor(predicate, startVersion, afterFirstUpdate, afterFirstUpdate);

      Delta delta = grid.change(cursor);
      Version versionBeforeMassUpdate = delta.getTable().getVersion();

      assertNotNull(delta);
      assertEquals(delta.getTable().getChanges().size(), 1);
      assertEquals(delta.getTable().getChanges().get(0).getChanges().size(), 1);
      assertEquals(delta.getTable().getChanges().get(0).getChanges().get(0).getColumn(), "dateOfBirth");
      assertEquals(delta.getTable().getChanges().get(0).getChanges().get(0).getValue(), "19/11/1967");

      ExampleObject[] inputs = new ExampleObject[INPUT_ITERATIONS];
      
      for(int i = 0; i < INPUT_ITERATIONS; i++) {
         inputs[i] = new ExampleObject("tom", "19/11/1967", "sydney" + i, "australia" + i, "trader", "some place");
      }      
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < INPUT_ITERATIONS; i++) {
         Message newMessage = new Message(inputs[i]);

         adapter.publish(newMessage);
      }
      long endTime = System.currentTimeMillis();

      System.err.println("Time taken to update " + INPUT_ITERATIONS + " objects was " + (endTime - startTime) + " milliseconds");

      Tuple finalObject = grid.find("tom");
      Map<String, Object> finalObjectAttributes = finalObject.getAttributes();      
      ExampleObject finalObjectObject = (ExampleObject) binder.toObject(finalObjectAttributes, ExampleObject.class.getName());

      assertNotNull(finalObjectObject);
      assertEquals(finalObjectObject.name, "tom");
      assertEquals(finalObjectObject.dateOfBirth, "19/11/1967");
      assertEquals(finalObjectObject.placeOfBirth, "sydney" + (INPUT_ITERATIONS - 1));
      assertEquals(finalObjectObject.country, "australia" + (INPUT_ITERATIONS - 1));
      assertEquals(finalObjectObject.job, "trader");
      assertEquals(finalObjectObject.address, "some place");

      Predicate finalPredicate = new PredicateParser("*");
      Cursor finalCursor = new Cursor(finalPredicate, startVersion, versionBeforeMassUpdate, versionBeforeMassUpdate);

      Delta finalDelta = grid.change(finalCursor);

      assertNotNull(finalDelta);
      assertEquals(finalDelta.getTable().getChanges().size(), 1);
      assertEquals(finalDelta.getTable().getChanges().get(0).getChanges().size(), 2);

      List<Cell> finalCells = finalDelta.getTable().getChanges().get(0).getChanges();
      List<String> finalNames = new ArrayList<String>();
      List<Object> finalValues = new ArrayList<Object>();

      for (Cell finalCell : finalCells) {
         String name = finalCell.getColumn();
         Object value = finalCell.getValue();

         finalNames.add(name);
         finalValues.add(value);
      }
      assertTrue(finalNames.contains("country"));
      assertTrue(finalValues.contains("australia" + (INPUT_ITERATIONS - 1)));
      assertTrue(finalNames.contains("placeOfBirth"));
      assertTrue(finalValues.contains("sydney" + (INPUT_ITERATIONS - 1)));
   }

   public void testGrid() throws Exception {
      String[] index = new String[] { "name" };
      Structure structure = new Structure(index);
      ExampleObjectMarshaller marshaller = new ExampleObjectMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();

      marshallers.put(ExampleObject.class.getName(), marshaller);

      ObjectBinder binder = new ObjectBinder(marshallers);
      Grid grid = new Grid(null, structure, ExampleObject.class.getName());
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(ExampleObject.class.getName(), grid);
      
      ExampleObject object = new ExampleObject("tom", "19/11/1987", "sydney", "australia", "trader", "some place");
      Version version = new Version();
      Message message = new Message(object);

      version.set(0L);
      adapter.publish(message);

      Tuple recovered = grid.find("tom");
      Map<String, Object> recoveredAttributes = recovered.getAttributes();      
      ExampleObject recoveredObject = (ExampleObject) binder.toObject(recoveredAttributes, ExampleObject.class.getName());

      assertNotNull(recoveredObject);
      assertEquals(recoveredObject.name, "tom");
      assertEquals(recoveredObject.dateOfBirth, "19/11/1987");
      assertEquals(recoveredObject.placeOfBirth, "sydney");
      assertEquals(recoveredObject.country, "australia");
      assertEquals(recoveredObject.job, "trader");
      assertEquals(recoveredObject.address, "some place");

      version.set(1L);

      ExampleObject updateForTom = new ExampleObject("tom", "19/11/1967", "sydney", "australia", "trader", "some place");
      Version versionBeforeUpdate = grid.change(new Cursor(new Any(), new Version(), new Version(), new Version())).getTable().getVersion();
      Message messageForTom = new Message(updateForTom);

      adapter.publish(messageForTom);

      Tuple updatedRecordForTom = grid.find("tom");
      Map<String, Object> updatedRecordForTomAttributes = updatedRecordForTom.getAttributes();      
      ExampleObject updatedRecordForTomObject = (ExampleObject) binder.toObject(updatedRecordForTomAttributes, ExampleObject.class.getName());

      assertNotNull(updatedRecordForTomObject);
      assertEquals(updatedRecordForTomObject.name, "tom");
      assertEquals(updatedRecordForTomObject.dateOfBirth, "19/11/1967");
      assertEquals(updatedRecordForTomObject.placeOfBirth, "sydney");
      assertEquals(updatedRecordForTomObject.country, "australia");
      assertEquals(updatedRecordForTomObject.job, "trader");
      assertEquals(updatedRecordForTomObject.address, "some place");

      Predicate predicate = new PredicateParser("*");
      Cursor cursor = new Cursor(predicate, version, versionBeforeUpdate, versionBeforeUpdate);

      Delta delta = grid.change(cursor);

      System.err.println(delta.getSchema().getColumns());

      assertNotNull(delta);
      assertEquals(delta.getTable().getChanges().size(), 1);
      assertEquals(delta.getTable().getChanges().get(0).getChanges().size(), 1);
      assertEquals(delta.getTable().getChanges().get(0).getChanges().get(0).getColumn(), "dateOfBirth");
      assertEquals(delta.getTable().getChanges().get(0).getChanges().get(0).getValue(), "19/11/1967");

   }

   private static class ExampleObject {

      public final String name;
      public final String dateOfBirth;
      public final String placeOfBirth;
      public final String country;
      public final String job;
      public final String address;

      public ExampleObject(String name, String dateOfBirth, String placeOfBirth, String country, String job, String address) {
         this.name = name;
         this.dateOfBirth = dateOfBirth;
         this.placeOfBirth = placeOfBirth;
         this.country = country;
         this.job = job;
         this.address = address;
      }
   }

   public static class ExampleObjectMarshaller implements ObjectMarshaller<ExampleObject> {

      @Override
      public Map<String, Object> fromObject(ExampleObject object) {
         Map<String, Object> data = new HashMap<String, Object>();

         data.put("name", object.name);
         data.put("dateOfBirth", object.dateOfBirth);
         data.put("placeOfBirth", object.placeOfBirth);
         data.put("country", object.country);
         data.put("job", object.job);
         data.put("address", object.address);

         return data;
      }

      @Override
      public ExampleObject toObject(Map<String, Object> data) {
         return new ExampleObject((String) data.get("name"), (String) data.get("dateOfBirth"), (String) data.get("placeOfBirth"), (String) data.get("country"), (String) data.get("job"),
               (String) data.get("address"));
      }

   }

}
