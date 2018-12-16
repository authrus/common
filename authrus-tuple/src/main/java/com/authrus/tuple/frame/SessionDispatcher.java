package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameType.MESSAGE;
import static com.authrus.tuple.frame.FrameType.QUERY;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

import com.authrus.io.ByteBufferBuilder;
import com.authrus.io.ByteBufferWriter;
import com.authrus.io.DataFormatter;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.queue.ElementBatch;

class SessionDispatcher {

   private final FrameConnection connection;
   private final SessionWriter writer;
   private final ByteBufferBuilder builder;
   private final ByteBufferWriter appender;
   private final DataFormatter formatter;
   private final FrameTracer tracer;
   private final Session session;

   public SessionDispatcher(FrameConnection connection, FrameTracer tracer, Session session) {
      this(connection, tracer, session, 8192);
   }   
   
   public SessionDispatcher(FrameConnection connection, FrameTracer tracer, Session session, int buffer) {
      this.formatter = new DataFormatter();
      this.writer = new SessionWriter(formatter);
      this.builder = new ByteBufferBuilder(buffer);
      this.appender = new ByteBufferWriter(builder);            
      this.connection = connection;
      this.session = session;
      this.tracer = tracer;   
   }   

   public synchronized int dispatch(Query query) {
      try {     
         if(session.isOpen()) {
            builder.order(LITTLE_ENDIAN);
            writer.writeQuery(appender, query);
   
            if (connection != null) {
               ByteBuffer data = builder.extract();
   
               if (data != null) {
                  Frame frame = new Frame(QUERY, data);
                  
                  tracer.onDispatch(session, frame);
                  return connection.send(frame);
               }
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not dispatch query", e);
      } finally {
         formatter.reset();
         builder.clear();
      }
      return -1;
   }
   
   public synchronized int dispatch(ElementBatch batch) {
      try {  
         if(session.isOpen()) {
            builder.order(LITTLE_ENDIAN);
            writer.writeBatch(appender, batch);
   
            if (connection != null) {
               ByteBuffer data = builder.extract();
   
               if (data != null) {
                  Frame frame = new Frame(MESSAGE, data);
                  
                  tracer.onDispatch(session, frame);
                  return connection.send(frame);
               }
            }
         }
      } catch (Exception e) {
         throw new IllegalStateException("Could not dispatch element batch", e);
      } finally {
         formatter.reset();
         builder.clear();
      }
      return -1;
   }
}
