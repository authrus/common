package com.authrus.tuple.frame;

import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import org.simpleframework.transport.Channel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.trace.TraceAgent;

public class FrameConnectionTest extends TestCase {
   
   public void testFrameConnection() throws Exception {
      DirectSocketBuilder builder = new DirectSocketBuilder(new TraceAgent(), "localhost", 23411);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      DirectTransportBuilder transportBuilder = new DirectTransportBuilder(builder, reactor);
      ExampleMonitor monitor = new ExampleMonitor();
      ExampleListener listener = new ExampleListener();
      FrameConnection connection = new FrameConnection(listener, monitor, transportBuilder);
      ServerSocket socket = new ServerSocket(23411);  
      
      connection.connect();
      
      Socket result = socket.accept();
      
      assertNotNull(result);
      Thread.sleep(5000);
      result.close();
      Thread.sleep(2000);
   }
   
   public static class ExampleListener implements FrameListener {

      @Override
      public void onConnect() {
         System.err.println("ExampleListener.onConnect");
      }

      @Override
      public void onFrame(Frame frame) {
         System.err.println("ExampleListener.onFrame"); 
      }

      @Override
      public void onException(Exception cause) {
         System.err.println("ExampleListener.onException");
         cause.printStackTrace();
      }

      @Override
      public void onSuccess(Sequence sequence) {
         System.err.println("ExampleListener.onSuccess");
      }

      @Override
      public void onHeartbeat() {
         System.err.println("ExampleListener.onHeartbeat"); 
      }

      @Override
      public void onClose() {
         System.err.println("ExampleListener.onClose");
      }
      
   }
   
   public static class ExampleMonitor extends FrameAdapter {

      @Override
      public void onConnect(Session session, Channel channel) {
         System.err.println("ExampleMonitor.onConnect");
      }

      @Override
      public void onException(Session session, Exception cause) {
         System.err.println("ExampleMonitor.onException");
      }

      @Override
      public void onSuccess(Session session, int sequence) {
         System.err.println("ExampleMonitor.onReceipt");
      }

      @Override
      public void onHeartbeat(Session session) {
         System.err.println("ExampleMonitor.onHeartbeat");
      }

      @Override
      public void onClose(Session session) {
         System.err.println("ExampleMonitor.onClose");
      }
      
   }

}
