package com.authrus.tuple.grid;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataDispatcher;
import com.authrus.io.DataFormatter;
import com.authrus.io.DataWriter;
import com.authrus.tuple.query.PredicateFilter;

public class DeltaProducer implements ChangeListener {

   private final CursorManager manager;
   private final ByteBufferBuilder builder;
   private final DataDispatcher dispatcher;
   private final DataFormatter formatter;
   private final DeltaValidator validator;
   private final DeltaWriter writer;
   private final DataWriter appender;   

   public DeltaProducer(DataDispatcher dispatcher, PredicateFilter filter) {      
      this.manager = new CursorManager(filter);
      this.validator = new DeltaValidator();
      this.builder = new ByteBufferBuilder();
      this.appender = new ByteBufferWriter(builder);
      this.formatter = new DataFormatter();
      this.writer = new DeltaWriter(formatter);
      this.dispatcher = dispatcher;
   }

   @Override
   public synchronized void onChange(Grid grid, Schema schema, String type) {
      Cursor cursor = manager.createCursor(grid, type);

      if (cursor != null) {
         try {
            Delta delta = grid.change(cursor);

            builder.order(LITTLE_ENDIAN);
            manager.updateCursor(grid, delta, type);
            validator.validate(delta, schema); 
            writer.writeDelta(appender, delta);

            if (dispatcher != null) {
               ByteBuffer frame = builder.extract();

               if (frame != null) {
                  dispatcher.dispatch(frame);
               }
            }
         } catch (Exception e) {
            throw new IllegalStateException("Could not dispatch delta", e);
         } finally {
            formatter.reset();  
            builder.clear();          
         }
      }
   }
}
