package com.authrus.tuple.queue;

import static com.authrus.tuple.frame.FrameType.HEARTBEAT;
import static com.authrus.tuple.frame.FrameType.MESSAGE;
import static com.authrus.tuple.frame.FrameType.QUERY;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.TransportProcessor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.io.DataReader;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.frame.Frame;
import com.authrus.tuple.frame.FrameCollector;
import com.authrus.tuple.frame.FrameDispatcher;
import com.authrus.tuple.frame.FrameEncoder;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.FrameSession;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.frame.FrameType;
import com.authrus.tuple.frame.Sequence;
import com.authrus.tuple.query.PredicateFilter;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.query.QueryReader;
import com.authrus.tuple.subscribe.SubscriptionListener;

class QueueProcessor implements TransportProcessor {

   private final SubscriptionListener listener;
   private final InsertSubscriber subscriber;
   private final AtomicBoolean active;
   private final FrameTracer tracer;
   private final Reactor reactor;
   
   public QueueProcessor(InsertSubscriber subscriber, SubscriptionListener listener, FrameTracer tracer, Reactor reactor) {
      this.active = new AtomicBoolean();
      this.subscriber = subscriber;
      this.listener = listener;      
      this.reactor = reactor;
      this.tracer = tracer;
   }
   
   @Override
   public void process(Transport transport) throws IOException {
      SocketChannel socket = transport.getChannel();
      SocketAddress address = socket.socket().getRemoteSocketAddress();
      String location = address.toString();
      
      if(socket.isConnected() && active.get()) {
         Channel channel = new TransportChannel(transport);
         FrameEncoder encoder = new FrameEncoder(channel);
         FrameListenerAdapter adapter = new FrameListenerAdapter(encoder, tracer, channel, location);
      
         adapter.onConnect();
      }
   }
   
   public void start() throws IOException {
      active.set(true);
   }

   @Override
   public void stop() throws IOException {
      active.set(false);
   }

   private class FrameListenerAdapter implements FrameListener {

      private final FrameDispatcher dispatcher;
      private final FrameCollector collector;
      private final ElementConsumer consumer;
      private final ElementListener adapter;
      private final FrameEncoder encoder;
      private final FrameSession session;      
      private final QueryReader reader;
      private final Channel channel;
      private final String address;
      private final Frame response;

      public FrameListenerAdapter(FrameEncoder encoder, FrameTracer tracer, Channel channel, String address) {
         this.session = new FrameSession(this, encoder, tracer, channel, address);    
         this.collector = new FrameCollector(this, null, session, channel, reactor);
         this.dispatcher = new FrameDispatcher(encoder, tracer, session);
         this.adapter = new ElementAdapter(address);
         this.consumer = new ElementConsumer(adapter);
         this.response = new Frame(HEARTBEAT);
         this.reader = new QueryReader();
         this.encoder = encoder;
         this.channel = channel;
         this.address = address;
      }

      @Override
      public void onConnect() { 
         try {
            tracer.onConnect(session, channel);
            listener.onConnect(address);
            reactor.process(collector);
         } catch(Exception e) {
            listener.onException(address, e);
         }
      } 

      @Override
      public void onFrame(Frame frame) {
         DataReader input = frame.getReader();
         FrameType type = frame.getType();

         try {
            if(type == QUERY) {
               Query query = reader.readQuery(input);            
               Map<String, String> predicates = query.getPredicates();
               SocketChannel socket = channel.getSocket();            
   
               if (predicates != null) {
                  PredicateFilter filter = new PredicateFilter(predicates);
                  ElementProducer producer = new ElementProducer(dispatcher, filter);
   
                  if (socket.isConnected()) {
                     listener.onSubscribe(address, query);
                     subscriber.subscribe(address, producer);
                  } else {
                     subscriber.cancel(address);
                  }
                  session.update(query);
               }
            } else if(type == MESSAGE) {
               consumer.consume(input);
            }
         } catch (Exception e) {
            listener.onException(address, e);
            subscriber.cancel(address);
            channel.close();       
         }         
      }     
      
      @Override
      public void onSuccess(Sequence sequence) {
         try {
            Frame frame = sequence.getFrame();
            
            if(frame != null) {
               encoder.encode(frame);
            }
         } catch(Exception e) {
            listener.onException(address, e);
            subscriber.cancel(address);
            channel.close();                
         }
      }      

      @Override
      public void onHeartbeat() {
         try {
            encoder.encode(response);
            listener.onHeartbeat(address);
         } catch(Exception e) {
            listener.onException(address, e);
            subscriber.cancel(address);
            channel.close();                
         }
      }      

      @Override
      public void onException(Exception cause) {
         tracer.onException(session, cause);
         listener.onException(address, cause);
         subscriber.cancel(address);
      }

      @Override
      public void onClose() {
         tracer.onClose(session);         
         listener.onClose(address);
         subscriber.cancel(address);
      }
   }
   
   private class ElementAdapter implements ElementListener {
      
      private final String address;

      public ElementAdapter(String address) {
         this.address = address;
      }
      
      @Override
      public void onElement(Element element) {
         Map<String, Object> attributes = element.getAttributes();
         String type = element.getType();
         
         if(attributes != null) {
            Tuple tuple = new Tuple(attributes, type);
            
            if(listener != null) {
               listener.onUpdate(address, tuple);
            }
         }   
      }      
   }    
}
