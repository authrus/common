package com.authrus.message.grid;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.Cursor;
import com.authrus.tuple.grid.Delta;
import com.authrus.tuple.grid.Grid;
import com.authrus.tuple.grid.GridPublisher;
import com.authrus.tuple.grid.Key;
import com.authrus.tuple.grid.KeyDelta;
import com.authrus.tuple.grid.RowDelta;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.TableDelta;
import com.authrus.tuple.grid.Version;

public class GridUpdateTest extends TestCase {

   public static class MessageObject {

      public final Map<String, String> fields;
      public final String key;

      public MessageObject(Map<String, String> fields, String key) {
         this.fields = fields;
         this.key = key;
      }

      public Map<String, String> getFields() {
         return fields;
      }

      public String getKey() {
         return key;
      }
   }

   private static class MessageMarshaller implements ObjectMarshaller<MessageObject> {

      @Override
      public Map<String, Object> fromObject(MessageObject message) {
         Map<String, Object> result = new LinkedHashMap<String, Object>();
         result.put("key", message.getKey());
         result.putAll(message.getFields());
         return result;
      }

      @Override
      public MessageObject toObject(Map<String, Object> message) {
         String key = (String) message.remove("key");
         Map<String, String> fields = new LinkedHashMap<String, String>();
         for (String name : message.keySet()) {
            fields.put(name, (String) message.get(name));
         }
         return new MessageObject(fields, key);
      }

   }

   public void testGrid() throws Exception {
      ObjectMarshaller marshaller = new MessageMarshaller();
      Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
      ObjectBinder binder = new ObjectBinder(marshallers);

      marshallers.put(MessageObject.class.getName(), marshaller);

      Structure structure = new Structure(new String[] { "key" });
      Grid grid = new Grid(null, structure, MessageObject.class.getName());
      Map<String, MessageObject> messages = new HashMap<String, MessageObject>();
      Map<String, Grid> grids = new HashMap<String, Grid>();
      Catalog catalog = new Catalog(grids);
      GridPublisher publisher = new GridPublisher(catalog);
      MessagePublisher adapter = new TupleMessagePublisher(publisher, binder); 
      
      grids.put(MessageObject.class.getName(), grid);
      
      for (int i = 0; i < 1000; i++) {
         Map<String, String> map = new HashMap<String, String>();
         map.put("key=" + i, "value-" + i);
         MessageObject messageObject = new MessageObject(map, "key-" + i);
         messages.put("key-" + i, messageObject);
         Message message = new Message(messageObject);

         adapter.publish(message);
      }
      Predicate predicate = new Any();
      Version version = new Version(-1);
      Cursor cursor = new Cursor(predicate, version, version, version);

      Delta delta = grid.change(cursor);
      TableDelta tableDelta = delta.getTable();
      List<RowDelta> rowDelta = tableDelta.getChanges();
      KeyDelta rowIndexDelta = tableDelta.getKeys();
      List<Key> rowIndexes = rowIndexDelta.getChanges();

      assertEquals("rows=" + rowDelta.size() + " indexes=" + rowIndexes.size(), rowDelta.size(), rowIndexes.size());

   }

}
