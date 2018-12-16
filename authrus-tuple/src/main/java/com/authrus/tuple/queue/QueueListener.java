package com.authrus.tuple.queue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.authrus.io.DataReader;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;
import com.authrus.tuple.frame.Frame;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.Sequence;

public class QueueListener implements FrameListener {

   private final AtomicReference<ElementConsumer> reference;
   private final AtomicReference<Sequence> counter;
   private final ElementAdapter listener;
   
   public QueueListener(TupleListener listener) {
      this.reference = new AtomicReference<ElementConsumer>();
      this.counter = new AtomicReference<Sequence>();
      this.listener = new ElementAdapter(listener);
   }

   @Override
   public void onConnect() {
      ElementConsumer consumer = new ElementConsumer(listener);

      reference.set(consumer);
      listener.onReset();
   }

   @Override
   public void onFrame(Frame frame) {
      ElementConsumer consumer = reference.get();
      DataReader input = frame.getReader();

      if (consumer == null) {
         throw new IllegalStateException("Frame received before connect notification");
      }
      try {
         consumer.consume(input);
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

   private class ElementAdapter implements ElementListener {
      
      private final TupleListener listener;
      
      public ElementAdapter(TupleListener listener) {
         this.listener = listener;
      }
   
      @Override
      public void onElement(Element element) {
         Map<String, Object> attributes = element.getAttributes();
         String type = element.getType();
         
         if(attributes != null) {
            Tuple tuple = new Tuple(attributes, type);
            
            if(listener != null) {
               listener.onUpdate(tuple);
            }
         }   
      }
      
      public void onException(Exception cause) {
         listener.onException(cause);
      }   
      
      public void onHeartbeat() {
         listener.onHeartbeat();
      }   
      
      public void onReset() {
         listener.onReset();
      }
   }   
}
