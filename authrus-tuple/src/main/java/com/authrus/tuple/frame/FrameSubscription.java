package com.authrus.tuple.frame;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.transport.Channel;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.collections.LeastRecentlyUsedMap.RemovalListener;
import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.query.QueryValidator;
import com.authrus.tuple.subscribe.Subscription;

public class FrameSubscription implements Subscription {

   private final AtomicReference<SessionContext> reference;
   private final AtomicReference<Query> criteria;
   private final Cache<Integer, Tuple> tuples;
   private final Set<Tuple> published;
   private final Set<Tuple> pending;
   private final FrameConnection connection;
   private final CapacityChecker checker;   
   private final QueryValidator validator;
   private final FrameListener listener;
   private final StatusMonitor monitor;
   private final AtomicBoolean active;
   
   public FrameSubscription(FrameListener listener, FrameTracer tracer, TransportBuilder builder) throws IOException {
      this(listener, tracer, builder, 5000);
   }

   public FrameSubscription(FrameListener listener, FrameTracer tracer, TransportBuilder builder, long retry) throws IOException {
      this(listener, tracer, builder, retry, 10000);
   }
   
   public FrameSubscription(FrameListener listener, FrameTracer tracer, TransportBuilder builder, long retry, int capacity) throws IOException {
      this.checker = new CapacityChecker(tracer);
      this.monitor = new StatusMonitor(tracer);
      this.connection = new FrameConnection(listener, monitor, builder, retry);
      this.tuples = new LeastRecentlyUsedCache<Integer, Tuple>(checker, capacity);
      this.published = new CopyOnWriteArraySet<Tuple>();         
      this.pending = new CopyOnWriteArraySet<Tuple>();        
      this.reference = new AtomicReference<SessionContext>();
      this.criteria = new AtomicReference<Query>();
      this.validator = new QueryValidator();
      this.active = new AtomicBoolean();
      this.listener = listener;
   }   
   
   public synchronized void subscribe(Query query) {
      try {
         if(!active.get()) {         
            validator.validate(query);
            active.set(true);
            criteria.set(query);
            connection.connect();
         }
      } catch(Exception cause) {
         listener.onException(cause);
      }
   }   

   @Override
   public synchronized void publish(Tuple tuple) {
      SessionContext context = reference.get();
      
      if (context != null) {
         SessionPublisher publisher = context.getPublisher();
         Session session = context.getSession();
            
         try {
            int sequence = publisher.publish(tuple);

            if(sequence >= 0) {
               published.add(tuple);                
               tuples.cache(sequence, tuple);  
            }
         } catch(Exception cause) {
            monitor.onException(session, cause);
            listener.onException(cause);
            session.close();
         } 
      }     
      pending.add(tuple); 
   }   

   @Override
   public synchronized void update(Query query) {
      SessionContext context = reference.get();
      
      if (context != null) {
         SessionPublisher publisher = context.getPublisher();
         Session session = context.getSession();
         
         validator.validate(query);          
            
         try {           
            publisher.publish(query);         
         } catch(Exception cause) {
            monitor.onException(session, cause);
            listener.onException(cause);
            session.close();
         }
      }
      criteria.set(query);
   }

   @Override
   public synchronized void cancel() {
      SessionContext context = reference.get();
      
      if (context != null) {
         Session session = context.getSession();
         
         try {
            if(!active.get()) {
               reference.set(null);
               active.set(false);
               connection.close();            
            }
         } catch(Exception cause) {
            monitor.onException(session, cause);
            listener.onException(cause);          
         }      
      }
   }
   
   private class CapacityChecker implements RemovalListener<Integer, Tuple> {
      
      private final FrameTracer tracer;

      public CapacityChecker(FrameTracer tracer) {
         this.tracer = tracer;
      }

      @Override
      public void notifyRemoved(Integer sequence, Tuple tuple) {
         SessionContext context = reference.get();         

         if(context != null) {
            if(pending.remove(tuple)) {
               Session session = context.getSession();
               
               try {
                  if(session != null) {                  
                     tracer.onFailure(session, sequence);
                  }
               } catch(Exception e) {
                  tracer.onException(session, e);
               }
            }
         }
         published.remove(tuple);         
      } 
   }
   
   private class StatusMonitor implements FrameTracer {
      
      private final FrameTracer tracer;

      public StatusMonitor(FrameTracer tracer) {
         this.tracer = tracer;
      }

      @Override
      public void onDispatch(Session session, Frame frame) {
         SessionContext context = reference.get();
         
         if (context != null) {
            Session current = context.getSession();
            
            if(current == session) {
               tracer.onDispatch(session, frame);
            }
         }
      }

      @Override
      public void onConnect(Session session, Channel channel) {
         Query query = criteria.get();
         
         if(session != null) {
            SessionDispatcher dispatcher = new SessionDispatcher(connection, tracer, session);
            SessionPublisher publisher = new SessionPublisher(dispatcher);
            SessionContext context = new SessionContext(publisher, session);
            
            if(query != null) {
               publisher.publish(query);
            }
            reference.set(context); 
         }
      }
      
      @Override
      public void onSuccess(Session session, int sequence) {
         SessionContext context = reference.get();
         
         if (context != null) {
            Session current = context.getSession();
            
            if(current == session) {
               Tuple tuple = tuples.take(sequence);
               
               if(tuple != null) {
                  try {                 
                     pending.remove(tuple);
                     published.remove(tuple);
                     tracer.onSuccess(session, sequence);
                  } catch(Exception e) {
                     tracer.onException(session, e);
                  }
               }
            }
         }
      }
      
      @Override
      public void onFailure(Session session, int sequence) {
         SessionContext context = reference.get();
         
         if (context != null) {
            Session current = context.getSession();
            
            if(current == session) {
               tracer.onFailure(session, sequence);
            }
         }
      }       

      @Override
      public void onException(Session session, Exception cause) {
         SessionContext context = reference.get();
         
         if (context != null) {
            Session current = context.getSession();
            
            if(current == session) {
               tracer.onException(session, cause);
               listener.onException(cause);
            }
         }         
      }
      
      @Override
      public void onHeartbeat(Session session) {       
         SessionContext context = reference.get();
         
         if (context != null) {
            SessionPublisher publisher = context.getPublisher();
            
            if(publisher != null) {
               for(Tuple tuple : pending) {
                  if(!published.contains(tuple)) {
                     int sequence = publisher.publish(tuple);
                  
                     if(sequence >= 0) { 
                        tuples.cache(sequence, tuple);               
                        published.add(tuple);
                     }
                  }
               }
               session.ping(); // causes an echo
            }
         }   
      }

      @Override
      public void onClose(Session session) {
         SessionContext context = reference.get();
         
         if (context != null) {
            Session current = context.getSession();
            
            if(current == session) {
               tuples.clear();
               published.clear();
            }
         }          
      }
   }
}
