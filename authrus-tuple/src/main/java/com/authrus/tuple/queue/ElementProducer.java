package com.authrus.tuple.queue;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataDispatcher;
import com.authrus.io.DataFormatter;
import com.authrus.predicate.Predicate;
import com.authrus.tuple.query.PredicateFilter;

class ElementProducer implements InsertListener {

   private final ByteBufferWriter appender;
   private final ByteBufferBuilder builder;
   private final DataDispatcher dispatcher;
   private final DataFormatter formatter;
   private final ElementWriter writer;
   private final ElementBatch matches;
   private final PredicateFilter filter;

   public ElementProducer(DataDispatcher dispatcher, PredicateFilter filter) {
      this.builder = new ByteBufferBuilder();
      this.appender = new ByteBufferWriter(builder);
      this.formatter = new DataFormatter();
      this.writer = new ElementWriter(formatter);
      this.matches = new ElementBatch();
      this.dispatcher = dispatcher;
      this.filter = filter;
   }

   @Override
   public synchronized void onInsert(ElementBatch batch, String type) {
      Predicate predicate = filter.getPredicate(type);

      if (predicate != null) {
         try {
            for (Element element : batch) {
               if (predicate.accept(element)) {
                  matches.insert(element);
               }
            }
            builder.order(LITTLE_ENDIAN);
            writer.writeBatch(appender, matches);

            if (dispatcher != null) {
               ByteBuffer frame = builder.extract();

               if (frame != null) {                 
                  dispatcher.dispatch(frame);
               }
            }
         } catch (Exception e) {
            throw new IllegalStateException("Could not dispatch element", e);
         } finally {
            formatter.reset();
            builder.clear();
            matches.clear();
         }
      }
   }
}
