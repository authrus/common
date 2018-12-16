package com.authrus.message.invoke;

import static com.authrus.message.invoke.ReturnStatus.SUCCESS;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationInterceptor implements InvocationHandler {

   private final OperationGenerator operationGenerator;
   private final InvocationPublisher publisher;
   private final InvocationTracer tracer;
   private final SignatureBuilder builder;
   private final Class type;

   public InvocationInterceptor(InvocationTracer tracer, InvocationPublisher publisher, Class type, String source) {
      this.operationGenerator = new OperationGenerator(source);
      this.builder = new SignatureBuilder();
      this.publisher = publisher;
      this.tracer = tracer;
      this.type = type;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] values) throws Throwable {
      String operation = operationGenerator.generateOperation();
      Invocation invocation = create(type, method, values);

      tracer.onInvoke(operation, invocation);

      return invoke(invocation, operation);
   }

   private Object invoke(Invocation invocation, String operation) throws Throwable {
      ReturnValue value = publisher.invoke(invocation, operation);

      try {
         ReturnStatus status = value.getStatus();
         Object object = value.getValue();

         if (status != SUCCESS) {
            if (object instanceof Throwable) {
               throw new IllegalStateException("Error from server", (Throwable) object);
            }
            throw new IllegalStateException("Error processing message " + status);
         }
         return object;
      } finally {
         tracer.onReturn(operation);
      }
   }

   private Invocation create(Class type, Method method, Object[] values) throws Throwable {
      String signature = builder.createSignature(type, method);
      Serializable[] arguments = convert(values);

      return new Invocation(type, signature, arguments);
   }

   private Serializable[] convert(Object[] values) throws Exception {
      if (values != null) {
         Serializable[] actual = new Serializable[values.length];

         for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
               Class type = values[i].getClass();

               if (!type.isPrimitive() && !type.isEnum()) {
                  if (!Serializable.class.isAssignableFrom(type)) {
                     throw new IllegalStateException("Parameter at index " + i + " is not serializable");
                  }
               }
               actual[i] = (Serializable) values[i];
            }
         }
         return actual;
      }
      return new Serializable[0];
   }

}
