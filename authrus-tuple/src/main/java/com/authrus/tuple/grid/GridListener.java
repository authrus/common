package com.authrus.tuple.grid;

import java.util.concurrent.atomic.AtomicReference;

import com.authrus.io.DataReader;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.Frame;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.Sequence;

class GridListener implements FrameListener {

   private final AtomicReference<DeltaDispatcher> reference;
   private final AtomicReference<Sequence> counter;
   private final TupleListener listener;
   private final long expiry;

   public GridListener(TupleListener listener) {
      this(listener, 30000);
   }
   
   public GridListener(TupleListener listener, long expiry) {
      this.reference = new AtomicReference<DeltaDispatcher>();
      this.counter = new AtomicReference<Sequence>();
      this.listener = listener;
      this.expiry = expiry;
   }

   @Override
   public void onConnect() {
      DeltaDispatcher consumer = new DeltaDispatcher(listener, expiry);

      reference.set(consumer);
      listener.onReset();
   }  

   @Override
   public void onFrame(Frame frame) {
      DeltaDispatcher consumer = reference.get();
      DataReader reader = frame.getReader();

      if (consumer == null) {
         throw new IllegalStateException("Frame received before connect notification");
      }
      try {
         consumer.consume(reader);
      } catch (Exception cause) {
         listener.onException(cause);
      }
   }

   @Override
   public void onException(Exception cause) {
      listener.onException(cause);
   }

   @Override
   public void onSuccess(Sequence sequence) {
      counter.set(sequence);
   }   

   @Override
   public void onHeartbeat() {
      listener.onHeartbeat();
   }

   @Override
   public void onClose() {
      reference.set(null);
      listener.onReset();
   }
}
