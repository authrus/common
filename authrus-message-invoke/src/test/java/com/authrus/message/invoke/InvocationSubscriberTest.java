package com.authrus.message.invoke;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;


public class InvocationSubscriberTest extends TestCase {

   public static interface ServiceInterface {
      String doMethodThatThrowsException();

      String doNoArgMethod();

      String doSomeMethod(String name, int code);
   }

   public static class ServiceImplementation implements ServiceInterface {

      @Override
      public String doSomeMethod(String name, int code) {
         return "doSomeMethod(" + name + "," + code + ")";
      }

      @Override
      public String doNoArgMethod() {
         return "doNoArgMethod()";
      }

      @Override
      public String doMethodThatThrowsException() {
         throw new RuntimeException("Exception processing method");
      }
   }

   public void testSubscriber() throws Exception {
      Map<Class, Object> services = new HashMap<Class, Object>();
      ServiceImplementation implementation = new ServiceImplementation();

      services.put(ServiceInterface.class, implementation);

      InvocationTracer tracer = new InvocationAdapter();
      InvocationDispatcher dispatcher = new InvocationDispatcher(services);
      TraceAgent analyzer = new TraceAgent();
      DirectSocketBuilder connector = new DirectSocketBuilder(analyzer, "localhost", 9776);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(connector, reactor);
      InvocationConnector subscriber = new InvocationConnector(tracer, dispatcher, transportBuilder, "someserver"); // this
                                                                                                    // is
                                                                                                    // the
                                                                                                    // listen
                                                                                                    // port

      subscriber.connect(); // connect to this port to listen
                                               // for RPC calls

      InvocationProxy builder = new InvocationProxy(tracer, pool, "someclient", 9776); // all
                                                                                 // RPC
                                                                                 // calles
                                                                                 // get
                                                                                 // sent
                                                                                 // to
                                                                                 // this
                                                                                 // port
      ServiceInterface proxy = builder.create(ServiceInterface.class);

      builder.start();

      Thread.sleep(1000); // we must wait for everything to connect before we
                          // invoke a method...

      assertEquals(proxy.doNoArgMethod(), "doNoArgMethod()");
   }
}
