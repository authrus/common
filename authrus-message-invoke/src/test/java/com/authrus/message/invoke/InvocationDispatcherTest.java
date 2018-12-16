package com.authrus.message.invoke;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.authrus.message.invoke.Invocation;
import com.authrus.message.invoke.InvocationDispatcher;
import com.authrus.message.invoke.ReturnStatus;
import com.authrus.message.invoke.ReturnValue;

import junit.framework.TestCase;

public class InvocationDispatcherTest extends TestCase {

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

   public void testMethodThatThrowsException() throws Exception {
      Map<Class, Object> objects = new HashMap<Class, Object>();
      ServiceInterface object = new ServiceImplementation();

      objects.put(ServiceInterface.class, object);

      InvocationDispatcher dispatcher = new InvocationDispatcher(objects);
      Invocation invocation = new Invocation(ServiceInterface.class, "com.authrus.message.invoke.InvocationDispatcherTest$ServiceInterface.doMethodThatThrowsException()", null);
      ReturnValue result = dispatcher.dispatch(invocation);

      assertEquals(result.getStatus(), ReturnStatus.EXCEPTION);
      assertTrue(Exception.class.isAssignableFrom(result.getValue().getClass()));
   }

   public void testMethodWithNoArguments() throws Exception {
      Map<Class, Object> objects = new HashMap<Class, Object>();
      ServiceInterface object = new ServiceImplementation();

      objects.put(ServiceInterface.class, object);

      InvocationDispatcher dispatcher = new InvocationDispatcher(objects);
      Invocation invocation = new Invocation(ServiceInterface.class, "com.authrus.message.invoke.InvocationDispatcherTest$ServiceInterface.doNoArgMethod()", null);
      ReturnValue result = dispatcher.dispatch(invocation);

      assertEquals(result.getStatus(), ReturnStatus.SUCCESS);
      assertEquals(result.getValue(), "doNoArgMethod()");
   }

   public void testMethodWithArguments() throws Exception {
      Map<Class, Object> objects = new HashMap<Class, Object>();
      ServiceInterface object = new ServiceImplementation();

      objects.put(ServiceInterface.class, object);

      InvocationDispatcher dispatcher = new InvocationDispatcher(objects);
      Invocation invocation = new Invocation(ServiceInterface.class, "com.authrus.message.invoke.InvocationDispatcherTest$ServiceInterface.doSomeMethod(java.lang.String, int)",
            new Serializable[] { "hello", 22 });
      ReturnValue result = dispatcher.dispatch(invocation);

      assertEquals(result.getStatus(), ReturnStatus.SUCCESS);
      assertEquals(result.getValue(), "doSomeMethod(hello,22)");
   }
}
