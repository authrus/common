package com.authrus.transport.tunnel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.ByteWriter;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.thread.ThreadPool;
import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.SocketBuilder;
import com.authrus.transport.TransportBuilder;
import com.authrus.transport.trace.TraceAgent;
import com.authrus.transport.trace.TraceLogger;

public class TunnelTest extends TestCase {   
   
   public void testSimpleTunnel() throws Exception {
      TraceLogger logger = new TraceLogger();
      TraceAgent analyzer = new TraceAgent(logger);
      SocketBuilder builder = new DirectSocketBuilder(analyzer, "localhost", 80);
      ThreadPool pool = new ThreadPool(10);
      Reactor reactor = new ExecutorReactor(pool);
      TunnelBuilder tunnelBuilder = new TunnelBuilder(builder, reactor, "www.google.com:9090", "/broker");      

      
      //TunnelServer serverSocket = new TunnelServer(25541);
      //serverSocket.start();
      TransportBuilder transportBuilder = new DirectTransportBuilder(tunnelBuilder, reactor);
      Transport transport = transportBuilder.connect();
      Channel channel = new TransportChannel(transport);
      ByteWriter writer = channel.getWriter();
      ByteCursor cursor = channel.getCursor();
      
      writer.write((
            "GET / HTTP/1.0\r\n"+
            "Host: localhost\r\n"+
            "\r\n"
            ).getBytes());
      writer.flush();
      byte[] chunk = new byte[20];
      while(cursor.isOpen()){
         if(cursor.isReady()){
            int count=cursor.read(chunk);
            
            System.err.write(chunk,0,count);
            System.err.flush();
            Thread.sleep(5000);
         }
      }
      
      writer.close();
      
      
      Thread.sleep(1000000000);
      /*String text = serverSocket.waitForClose();
      System.err.println(text);
      assertTrue(text.contains("CONNECT"));
      assertTrue(text.contains("OK READY!!"));*/
   }
   
   private static class TunnelServer extends Thread {

      private ByteArrayOutputStream buffer;
      private ServerSocket serverSocket;
      private CountDownLatch latch;
      
      public TunnelServer(int port) throws IOException {
         this.serverSocket = new ServerSocket(port);
         this.buffer = new ByteArrayOutputStream();
         this.latch = new CountDownLatch(1);
      }
      
      public String waitForClose(){
         try{
            latch.await(5000, TimeUnit.MILLISECONDS);            
         }catch(Exception e){
            e.printStackTrace();
         }
         return buffer.toString();
      }
      
      public void run() {
         try {
            Socket socket = serverSocket.accept();
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            out.write((
                  "HTTP/1.1 200 OK\r\n"+
                   "Server: Test/1.0\r\n"+
                   "Connection: keep-alive\r\n"+
                   "\r\n"
                  ).getBytes());
            out.flush();
            int count = 0;
            while((count = in.read())!=-1){
               buffer.write(count);
               System.err.write(count);
               System.err.flush();
            }            
            System.err.println("CLOSED!!");
         }catch(Exception e){
            e.printStackTrace();
         }finally{
            latch.countDown();
         }
      }
   }
}
