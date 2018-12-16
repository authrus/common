package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameType.HEARTBEAT;
import static com.authrus.tuple.frame.FrameType.RECEIPT;

import java.io.IOException;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Channel;

import com.authrus.io.DataReader;

class FrameProcessor {
   
   private final FrameListener listener;
   private final FrameConsumer consumer;
   private final FrameTracer tracer;
   private final ByteCursor cursor;   
   private final Channel channel; 
   private final Session session;
   
   public FrameProcessor(FrameListener listener, FrameTracer tracer, Session session, Channel channel) {
      this.consumer = new FrameConsumer();
      this.cursor = channel.getCursor();
      this.listener = listener;
      this.channel = channel;
      this.tracer = tracer;
      this.session = session;
   }   

   public void process() throws IOException {
      while(cursor.isReady()) {
         consumer.consume(cursor);
         
         if(consumer.isFinished()) {
            try {
               Frame frame = consumer.getFrame();
               FrameType type = frame.getType();
               
               if(type == RECEIPT) {
                  DataReader reader = frame.getReader();
                  int received = reader.readInt();                  
                  
                  if(tracer != null) {
                     tracer.onSuccess(session, received);
                  }
               } else if (type == HEARTBEAT) {               
                  listener.onHeartbeat();
               } else {
                   Sequence sequence = consumer.getSequence();

                   try {
                      listener.onFrame(frame);
                   } finally {
                      listener.onSuccess(sequence);
                   }
               }
            } catch (Exception e) {
               listener.onException(e);
               tracer.onClose(session);
               channel.close();
            } finally {
               consumer.clear();
            }
         }
      }
   }
   
   public void failure(Exception cause) throws IOException {
      if(tracer != null) {
         tracer.onClose(session);
      }
      listener.onException(cause);
   }
   
   public void close() throws IOException {
      if(tracer != null) {
         tracer.onClose(session);
      }
      listener.onClose();
      channel.close();
   }
}
