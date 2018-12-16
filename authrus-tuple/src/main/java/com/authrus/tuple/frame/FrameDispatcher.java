package com.authrus.tuple.frame;

import static com.authrus.tuple.frame.FrameType.MESSAGE;

import java.nio.ByteBuffer;

import com.authrus.io.DataDispatcher;

public class FrameDispatcher implements DataDispatcher {

   private final FrameEncoder encoder;
   private final FrameTracer tracer;
   private final Session session;

   public FrameDispatcher(FrameEncoder encoder, FrameTracer tracer, Session session) {
      this.session = session;
      this.encoder = encoder;
      this.tracer = tracer;
   }

   @Override
   public synchronized void dispatch(ByteBuffer buffer) {
      Frame frame = new Frame(MESSAGE, buffer);

      try {
         encoder.encode(frame);
         
         if(tracer != null) {
            tracer.onDispatch(session, frame);
         }
      } catch (Exception e) {
         try {
            encoder.close();
         } catch (Throwable t) {
            throw new IllegalStateException("Could not close connection", t);
         }
         throw new IllegalStateException("Could not dispatch frame", e);
      }
   }
}
