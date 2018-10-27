package com.authrus.gateway.test;

import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLContext;

import junit.framework.TestCase;
import lombok.SneakyThrows;

import org.simpleframework.common.thread.ConcurrentExecutor;
import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;
import org.simpleframework.transport.trace.Trace;
import org.simpleframework.transport.trace.TraceAnalyzer;

import com.zuooh.common.ssl.Certificate;
import com.zuooh.common.ssl.DefaultCertificate;
import com.zuooh.http.proxy.balancer.Connection;
import com.zuooh.http.proxy.balancer.connect.ConnectionBuilder;
import com.zuooh.http.proxy.balancer.connect.SecureConnectionBuilder;

public class PingCheckerTest extends TestCase {

   @SneakyThrows
   public void testSecureConnection() {
      checkWithSocket("www.google.com", 443);
      checkWithConnection("www.google.com", 443);
   }
   
   @SneakyThrows
   public void checkWithSocket(String host, int port) {
      Socket socket = SSLContext.getDefault().getSocketFactory().createSocket(host, port);
      socket.getOutputStream().write(("GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n").getBytes());
      InputStream in = socket.getInputStream();
      byte[] terminal = new byte[]{'\r', '\n', '\r', '\n'};
      int count = 0;
      int seek = 0;
      
      while((count = in.read())!=-1) {
         byte octet = (byte)count;
         if(octet != terminal[seek++]) {
            seek = 0;
         }
         System.err.write((char)octet);
         if(seek == terminal.length) {
            break;
         }
      }
      socket.close();
   }
   
   @SneakyThrows
   public void checkWithConnection(String host, int port) {
      TraceAnalyzer analyzer = new TraceAnalyzer() {

         @Override
         public Trace attach(SelectableChannel channel) {
            return new Trace() {

               @Override
               public void trace(Object event) {
                  System.err.println(event);
               }

               @Override
               public void trace(Object event, Object value) {
                  System.err.println(event + ": " + value);
               }
               
            };
         }

         @Override
         public void stop() {}
         
      };
      Certificate certificate = new DefaultCertificate();
      Executor executor = new ConcurrentExecutor(Runnable.class);
      Reactor reactor = new ExecutorReactor(executor);
      ConnectionBuilder builder = new SecureConnectionBuilder(analyzer, certificate, reactor, host, port, 8192);
      Connection connection = builder.build();
      
      connection.getWriter().write(("GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n").getBytes());
      ByteCursor cursor = connection.getCursor();
      byte[] data = new byte[8192];
      
      while(cursor.isReady()) {
         int count = cursor.read(data);
         System.out.write(data, 0, count);
      }
   }
}
