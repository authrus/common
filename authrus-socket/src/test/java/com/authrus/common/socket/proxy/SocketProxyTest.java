package com.authrus.common.socket.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.SocketAcceptor;
import com.authrus.common.socket.SocketConnector;
import com.authrus.common.socket.proxy.analyser.AnalyserProxy;
import com.authrus.common.socket.proxy.analyser.CombinationAnalyser;
import com.authrus.common.socket.proxy.analyser.PacketAnalyser;

public class SocketProxyTest extends TestCase {

   private static class ByteAccumulator extends Thread {

      private final ByteArrayOutputStream buffer;
      private final Connection connection;

      public ByteAccumulator(Connection connection) throws IOException {
         this.buffer = new ByteArrayOutputStream();
         this.connection = connection;
      }

      public byte[] getAccumulated() {
         return buffer.toByteArray();
      }

      public void run() {
         try {
            InputStream input = connection.getInputStream();
            byte[] data = new byte[1024];
            int count = 0;

            while ((count = input.read(data)) != -1) {
               buffer.write(data, 0, count);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      public void close() {
         connection.close();
      }
   }

   private static class ByteAccumulatorServer implements Runnable {

      private BlockingQueue<ByteAccumulator> accumulators;
      private SocketAcceptor acceptor;
      private AtomicBoolean active;
      private Thread thread;

      public ByteAccumulatorServer(int port) throws IOException {
         this.accumulators = new LinkedBlockingQueue<ByteAccumulator>();
         this.acceptor = new SocketAcceptor(port);
         this.active = new AtomicBoolean();
      }

      public ByteAccumulator nextAccumulator() throws InterruptedException {
         return accumulators.take();
      }

      public boolean isRunning() {
         return active.get();
      }

      public void start() {
         if (!isRunning()) {
            thread = new Thread(this);
            thread.setName(ByteAccumulatorServer.class.getSimpleName());
            active.set(true);
            thread.start();
         }
      }

      public void stop() {
         active.set(false);
         acceptor.close();
      }

      public void join() throws InterruptedException {
         if (thread != null && thread.isAlive()) {
            thread.join();
         }
      }

      public void run() {
         while (active.get()) {
            try {
               Connection connection = acceptor.accept();

               if (connection.isConnected()) {
                  ByteAccumulator accumulator = new ByteAccumulator(connection);
                  accumulators.offer(accumulator);
                  accumulator.start();
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   public void testSocketProxy() throws Exception {
      SocketAcceptor acceptor = new SocketAcceptor(21122);
      SocketConnector connector = new SocketConnector("localhost", 21123);
      PacketAnalyser inputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      PacketAnalyser outputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      SocketProxy proxy = new AnalyserProxy(inputAnalyser, outputAnalyser);
      SocketProxyServer server = new SocketProxyServer(proxy, acceptor, connector);
      ByteAccumulatorServer accumulatorServer = new ByteAccumulatorServer(21123);

      server.start();
      accumulatorServer.start();

      ByteArrayOutputStream sent = new ByteArrayOutputStream();
      SocketConnector client = new SocketConnector("localhost", 21122);
      Connection connection = client.connect();
      OutputStream output = connection.getOutputStream();

      for (int i = 0; i < 10000; i++) {
         output.write(i);
         sent.write(i);
      }
      connection.close();

      ByteAccumulator accumulator = accumulatorServer.nextAccumulator();

      accumulator.join();
      accumulator.close();

      byte[] expected = sent.toByteArray();
      byte[] actual = accumulator.getAccumulated();

      assertEquals(expected.length, actual.length);

      for (int i = 0; i < expected.length; i++) {
         assertEquals(expected[i], actual[i]);
      }
      System.err.printf("expected=%s actual=%s%n", expected.length, actual.length);

      acceptor.close();
      accumulatorServer.stop();
      accumulatorServer.join();
   }

   public void testConnectionChurn() throws Exception {
      final SocketAcceptor acceptor = new SocketAcceptor(21124, 200);
      final SocketConnector connector = new SocketConnector("localhost", 21125);
      final PacketAnalyser inputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      final PacketAnalyser outputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      final SocketProxy proxy = new AnalyserProxy(inputAnalyser, outputAnalyser);
      final SocketProxyServer server = new SocketProxyServer(proxy, acceptor, connector);
      final ByteAccumulatorServer accumulatorServer = new ByteAccumulatorServer(21125);
      final ByteArrayOutputStream packet = new ByteArrayOutputStream();
      final SecureRandom random = new SecureRandom();

      for (int i = 0; i < 10000; i++) {
         int octet = random.nextInt(255);
         packet.write(octet);
      }

      final byte[] data = packet.toByteArray();

      server.start();
      accumulatorServer.start();

      for (int i = 0; i < 100; i++) {
         Thread thread = new Thread(new Runnable() {
            public void run() {
               try {
                  SocketConnector client = new SocketConnector("localhost", 21124);
                  Connection connection = client.connect();
                  OutputStream output = connection.getOutputStream();

                  for (int i = 0; i < data.length; i++) {
                     output.write(data[i]);
                  }
                  connection.close();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         });
         thread.start();
      }

      for (int i = 0; i < 100; i++) {
         ByteAccumulator accumulator = accumulatorServer.nextAccumulator();
         accumulator.join();

         byte[] actual = accumulator.getAccumulated();

         assertEquals(data.length, actual.length);

         for (int j = 0; j < data.length; j++) {
            assertEquals(data[j], actual[j]);
         }
         System.err.printf("[" + i + " of " + 100 + "] expected=%s actual=%s%n", data.length, actual.length);
      }
      acceptor.close();
      accumulatorServer.stop();
      accumulatorServer.join();
   }
}
