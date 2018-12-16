package com.authrus.message.invoke;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class InvocationConnection {

   private final AtomicReference<Closeable> reference;
   private final InvocationConnector connector;
   private final boolean enable;

   public InvocationConnection(InvocationConnector connector) {
      this(connector, true);
   }
   
   public InvocationConnection(InvocationConnector connector, boolean enable) {
      this.reference = new AtomicReference<Closeable>();
      this.connector = connector;
      this.enable = enable;
   }

   public void connect() throws IOException {
      if(enable) {
         Closeable connection = connector.connect();
         
         if(connection != null) {
            reference.set(connection);
         }
      }
   }
   
   public void close() throws IOException {
      if(enable) {
         Closeable connection = reference.get();
         
         if(connection != null) {
            connection.close();
         }
      }
   }
}
