package com.authrus.message.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.io.Base64InputStream;
import com.authrus.common.io.Base64OutputStream;
import com.authrus.common.thread.ThreadPool;
import com.authrus.message.Message;
import com.authrus.message.MessageListener;
import com.authrus.message.MessagePublisher;
import com.authrus.message.bind.ObjectBinder;
import com.authrus.message.bind.ObjectMarshaller;
import com.authrus.message.tuple.TupleMessageListener;
import com.authrus.message.tuple.TupleMessagePublisher;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.tuple.Tuple;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.query.Origin;
import com.authrus.tuple.query.Query;
import com.authrus.tuple.queue.InsertSubscriber;
import com.authrus.tuple.queue.Queue;
import com.authrus.tuple.queue.QueuePublisher;
import com.authrus.tuple.queue.QueueServer;
import com.authrus.tuple.queue.QueueSubscriber;
import com.authrus.tuple.subscribe.Subscriber;
import com.authrus.tuple.subscribe.Subscription;
import com.authrus.tuple.subscribe.SubscriptionListener;
import com.authrus.tuple.subscribe.SubscriptionLogger;

public class ReciprocalSubscriptionTest extends TestCase {

   private static final int CLIENT_PORT = 12980;
   private static final int SERVER_PORT = 23443;

   public void testReciprocalSubscriptions() throws Exception {
      InvokingClientService invokingService = new InvokingClientService(CLIENT_PORT);
      InvocationReceiverService invocationReveiverService = new InvocationReceiverService(SERVER_PORT, CLIENT_PORT);

      for (int i = 0; i < 10; i++) {
         Thread.sleep(5000);
         invokingService.invokeMethod("invokeSomeMethod");
      }
   }

   public static class SomeExampleServiceToBeInvoked {

      public String invokeSomeMethod() {
         return "some method was invoked at " + new Date();
      }
   }

   public static class SerializationMarshaller implements ObjectMarshaller<Object> {

      private final String name;

      public SerializationMarshaller(String name) {
         this.name = name;
      }

      @Override
      public Map<String, Object> fromObject(Object object) {
         try {
            Map<String, Object> attributes = new HashMap<String, Object>();
            OutputStream encoder = new Base64OutputStream(1024);
            ObjectOutput serializer = new ObjectOutputStream(encoder);

            serializer.writeObject(object);
            serializer.close();

            String data = encoder.toString();

            attributes.put(name, data);
            return attributes;
         } catch (Exception e) {
            throw new IllegalStateException("Could not serialize", e);
         }
      }

      @Override
      public Object toObject(Map<String, Object> message) {
         String payload = (String) message.get(name);

         try {
            InputStream decoder = new Base64InputStream(payload);
            ObjectInput deserializer = new ObjectInputStream(decoder);

            return deserializer.readObject();
         } catch (Exception e) {
            throw new IllegalStateException("Could not serialize", e);
         }
      }

   }

   public static class MethodInvocation implements Serializable {

      private String methodName;

      public MethodInvocation(String methodName) {
         this.methodName = methodName;
      }

      @Override
      public String toString() {
         return "INVOKE " + methodName;
      }
   }

   public static class MethodInvocationResponse implements Serializable {

      private String methodResponse;

      public MethodInvocationResponse(String methodResponse) {
         this.methodResponse = methodResponse;
      }

      @Override
      public String toString() {
         return "RESPONSE " + methodResponse;
      }
   }

   public static class MethodInvocationListener implements MessageListener {

      private final SomeExampleServiceToBeInvoked someService;
      private final MessagePublisher responsePublisher;

      private MethodInvocationListener(SomeExampleServiceToBeInvoked someService, MessagePublisher responsePublisher) {
         this.responsePublisher = responsePublisher;
         this.someService = someService;
      }

      @Override
      public void onMessage(Message message) {
         Object value = message.getValue();
         String response = someService.invokeSomeMethod();

         System.err.println("MethodInvocationListener.onMessage(" + value + ")");

         responsePublisher.publish(new Message(new MethodInvocationResponse(response)));
      }

      @Override
      public void onException(Exception cause) {
         System.err.print("MethodInvocationListener.onException(");
         cause.printStackTrace(System.err);
         System.err.println(")");
      }

      @Override
      public void onHeartbeat() {
         System.err.println("MethodInvocationListener.onHeartbeat()");
      }

      @Override
      public void onReset() {
         System.err.println("MethodInvocationListener.onReset()");
      }
   }

   public static class MethodInvocationResponseListener implements MessageListener {

      @Override
      public void onMessage(Message message) {
         Object value = message.getValue();
         System.err.println("MethodInvocationResponseListener.onMessage(" + value + ")");
      }

      @Override
      public void onException(Exception cause) {
         System.err.print("MethodInvocationResponseListener.onException(");
         cause.printStackTrace(System.err);
         System.err.println(")");
      }

      @Override
      public void onHeartbeat() {
         System.err.println("MethodInvocationResponseListener.onHeartbeat()");
      }

      @Override
      public void onReset() {
         System.err.println("MethodInvocationResponseListener.onReset()");
      }
   }

   public static class InvokingClientService implements SubscriptionListener {

      private final Map<String, Subscription> subscriptions;
      private final MethodInvocationResponseListener listener;
      private final FrameTracer monitor;
      private final ObjectBinder binder;
      private final Queue queue;
      private final MessagePublisher publisher;
      private final int listenPort;

      public InvokingClientService(int listenPort) throws IOException {
         ThreadPool pool = new ThreadPool(5);
         InsertSubscriber subscriber = new InsertSubscriber(pool);
         SessionRegistry checker = new SessionRegistry();
         Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
         ObjectBinder binder = new ObjectBinder(marshallers);
         SessionRegistryListener registryListener = new SessionRegistryListener(checker);
         QueueServer server = new QueueServer(subscriber, this, registryListener, listenPort);
         SerializationMarshaller marshaller = new SerializationMarshaller("object");
         Map<String, Queue> queues = new HashMap<String, Queue>();
         QueuePublisher publisher = new QueuePublisher(queues);
         Queue queue = new Queue(subscriber, MethodInvocation.class.getName());
         
         queues.put(MethodInvocation.class.getName(), queue);
         queues.put(MethodInvocationResponse.class.getName(), queue);
         
         marshallers.put(MethodInvocation.class.getName(), marshaller);
         marshallers.put(MethodInvocationResponse.class.getName(), marshaller);
         
         this.publisher = new TupleMessagePublisher(publisher, binder);
         this.listener = new MethodInvocationResponseListener();
         this.subscriptions = new ConcurrentHashMap<String, Subscription>();
         this.monitor = registryListener;
         this.listenPort = listenPort;
         this.binder = binder;
         this.queue = queue;

         server.start();
      }

      public void invokeMethod(String name) {
         MethodInvocation invocation = new MethodInvocation(name);
         Message message = new Message(invocation);

         publisher.publish(message);
      }

      @Override
      public void onException(String address, Exception cause) {
         Subscription subscription = subscriptions.get(address);

         if (subscription != null) {
            subscription.cancel();
         }
      }

      @Override
      public void onSubscribe(String address, Query query) {
         Subscription subscription = subscriptions.get(address);
         Origin origin = query.getOrigin();

         if (subscription == null) {
            try {
               TraceAgent analyzer = new TraceAgent();
               DirectSocketBuilder connector = new DirectSocketBuilder(analyzer, origin.getHost(), origin.getPort());
               ThreadPool pool = new ThreadPool(10);
               Reactor reactor = new ExecutorReactor(pool);
               DirectTransportBuilder transportBuilder = new DirectTransportBuilder(connector, reactor);
               Subscriber subscriber = new QueueSubscriber(monitor, transportBuilder);
               Map<String, String> predicates = new HashMap<String, String>();
               Origin myOrigin = new Origin("test", "localhost", listenPort);
               Query responseQuery = new Query(myOrigin, predicates);
               TupleMessageListener adapterListener = new TupleMessageListener(listener, binder);
               
               predicates.put(MethodInvocationResponse.class.getName(), "*"); // for not
                                                                    // subscribe to
                                                                    // all
                                                                    // responses
               
               subscription = subscriber.subscribe(adapterListener, responseQuery);
               subscriptions.put(address, subscription);
            } catch(Exception e){
               e.printStackTrace();
            }
         }
      }

      @Override
      public void onHeartbeat(String address) {
         Subscription subscription = subscriptions.get(address);

         if (subscription == null) {
            System.err.println("onHeartbeat(" + address + ") BAD (unknown subscription???????)");
         } else {
            System.err.println("onHeartbeat(" + address + ") GOOD");
         }
      }

      @Override
      public void onConnect(String address) {
         Subscription subscription = subscriptions.get(address);

         if (subscription != null) {
            System.err.println("onConnect(" + address + ") BAD (we already have a subscription from here???????)");
         } else {
            System.err.println("onConnect(" + address + ") GOOD");
         }
      }

      @Override
      public void onClose(String address) {
         Subscription subscription = subscriptions.remove(address);

         if (subscription == null) {
            System.err.println("onClose(" + address + ") BAD (unknown subscription???????)");
         } else {
            System.err.println("onClose(" + address + ") GOOD (cancelling now)");
            subscription.cancel();
         }
      }

      @Override
      public void onUpdate(String address, Tuple tuple) {
         Subscription subscription = subscriptions.get(address);

         if (subscription == null) {
            System.err.println(" onFrame(" + address + ") BAD (unknown subscription???????)");
         } else {
            System.err.println(" onFrame(" + address + ") GOOD");
         } 
      }
   }

   public static class InvocationReceiverService {

      public InvocationReceiverService(int listenPort, int subscribePort) throws IOException {
         ThreadPool pool = new ThreadPool(10);
         InsertSubscriber subscriber = new InsertSubscriber(pool);
         SubscriptionListener logger = new SubscriptionLogger();
         SessionRegistry checker = new SessionRegistry();
         SessionRegistryListener registryListener = new SessionRegistryListener(checker);
         Map<String, ObjectMarshaller> marshallers = new HashMap<String, ObjectMarshaller>();
         ObjectBinder binder = new ObjectBinder(marshallers);
         QueueServer server = new QueueServer(subscriber, logger, registryListener, listenPort);
         SerializationMarshaller marshaller = new SerializationMarshaller("object");

         marshallers.put(MethodInvocation.class.getName(), marshaller);
         marshallers.put(MethodInvocationResponse.class.getName(), marshaller);

         SomeExampleServiceToBeInvoked someService = new SomeExampleServiceToBeInvoked();
         SessionRegistry monitor = new SessionRegistry();
         Queue queue = new Queue(subscriber, MethodInvocationResponse.class.getName()); // This
                                                                                      // is
                                                                                      // probably
                                                                                      // not
                                                                                      // a
                                                                                      // great
                                                                                      // thing
                                                                                      // as
                                                                                      // it
                                                                                      // can
                                                                                      // be
                                                                                      // confusing.....
         Map<String, Queue> queues = new HashMap<String, Queue>();
         QueuePublisher responsePublisher = new QueuePublisher(queues);
         TraceAgent analyzer = new TraceAgent();
         DirectSocketBuilder connector = new DirectSocketBuilder(analyzer, "localhost", subscribePort);
         Reactor reactor = new ExecutorReactor(pool);
         DirectTransportBuilder transportBuilder = new DirectTransportBuilder(connector, reactor);
         QueueSubscriber invocationSubscriber = new QueueSubscriber(registryListener, transportBuilder);
         TupleMessagePublisher adapterPublisher = new TupleMessagePublisher(responsePublisher, binder);
         MethodInvocationListener listener = new MethodInvocationListener(someService, adapterPublisher);
         TupleMessageListener adapterListener = new TupleMessageListener(listener, binder);
         Map<String, String> predicates = new HashMap<String, String>();
         Origin origin = new Origin("receiver", "localhost", listenPort);
         Query query = new Query(origin, predicates);

         predicates.put(MethodInvocation.class.getName(), "*");
         invocationSubscriber.subscribe(adapterListener, query);
         queues.put(MethodInvocationResponse.class.getName(), queue);

         server.start();
      }
   }
}
