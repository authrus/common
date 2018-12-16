package com.authrus.message.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.message.Message;
import com.authrus.message.MessageAdapter;
import com.authrus.message.MessageListener;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.TupleListener;

public class TupleMessageListener extends MessageAdapter implements TupleListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(TupleMessageListener.class);
   
   private final List<MessageListener> listeners;
   private final ObjectBinder binder;

   public TupleMessageListener(MessageListener listener, ObjectBinder binder) {
      this(Arrays.asList(listener), binder);
   }
   
   public TupleMessageListener(List<MessageListener> listeners, ObjectBinder binder) {
      this.listeners = listeners;
      this.binder = binder;
   }

   @Override
   public void onUpdate(Tuple tuple) {
      String type = tuple.getType();
      Map<String, Object> attributes = tuple.getAttributes();
      Object value = binder.toObject(attributes, type);
      
      if(value != null) {
         Message message = new Message(value, attributes);
         
         for(MessageListener listener : listeners) {
            try {            
               listener.onMessage(message);
            } catch(Exception e) {
               LOG.info("Could not dispatch " + tuple, e);
            }
         }
      }
   }   
   
   @Override
   public void onMessage(Message message) {
      for(MessageListener listener : listeners) {
         try {            
            listener.onMessage(message);
         } catch(Exception e) {
            LOG.info("Could not dispatch " + message, e);
         }
      }
   }   

   @Override
   public void onException(Exception cause) {
      for(MessageListener listener : listeners) {
         try {            
            listener.onException(cause);
         } catch(Exception e) {
            LOG.info("Could not dispatch exception " + cause, e);
         }
      }
   }

   @Override
   public void onHeartbeat() {
      for(MessageListener listener : listeners) {
         try {            
            listener.onHeartbeat();
         } catch(Exception e) {
            LOG.info("Could not dispatch heartbeat", e);
         }
      }
   }

   @Override
   public void onReset() {
      for(MessageListener listener : listeners) {
         try {            
            listener.onReset();
         } catch(Exception e) {
            LOG.info("Could not dispatch reset", e);
         }
      }
   }

}
