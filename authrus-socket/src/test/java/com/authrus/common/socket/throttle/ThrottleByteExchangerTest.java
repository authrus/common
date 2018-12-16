package com.authrus.common.socket.throttle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.SocketAcceptor;
import com.authrus.common.socket.SocketConnector;
import com.authrus.common.socket.proxy.SocketProxy;
import com.authrus.common.socket.proxy.SocketProxyServer;
import com.authrus.common.socket.proxy.analyser.AnalyserProxy;
import com.authrus.common.socket.proxy.analyser.CombinationAnalyser;
import com.authrus.common.socket.proxy.analyser.PacketAnalyser;

public class ThrottleByteExchangerTest extends TestCase {

   private static final long TEST_CAPACITY = 1800000L;
   private static final int TEST_FREE_PERCENTAGE = 50;
   private static final int TEST_ALERT_PERCENTAGE = 40;

   public void testByteExchander() throws Exception {
      ThrottleCapacity capacity = new ThrottleCapacity(TEST_CAPACITY, TEST_FREE_PERCENTAGE, TEST_ALERT_PERCENTAGE);
      ThrottleRegistry registry = new ThrottleRegistry(capacity);
      Throttler throttler = new Throttler(registry, registry);
      SocketAcceptor acceptor = new SocketAcceptor(21128);
      ThrottleAcceptor throttleAcceptor = new ThrottleAcceptor(throttler, acceptor);
      SocketConnector connector = new SocketConnector("localhost", 21129);
      PacketAnalyser inputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      PacketAnalyser outputAnalyser = new CombinationAnalyser(Collections.EMPTY_LIST);
      SocketProxy proxy = new AnalyserProxy(inputAnalyser, outputAnalyser);
      SocketProxyServer server = new SocketProxyServer(proxy, throttleAcceptor, connector);
      ByteCounterServer counter = new ByteCounterServer(21129);

      server.start();
      counter.start();

      SocketConnector client = new SocketConnector("localhost", 21128);
      Connection connection = client.connect();
      ByteSender sender = new ByteSender(connection, 1, TEST_CAPACITY);

      sender.start();
      sender.join();

      System.err.println("Time taken to send all of the data was " + sender.getTimeTaken());

      ByteCounter byteCounter = counter.nextCounter();

      byteCounter.join();

      long senderStart = sender.getStartTime();
      long counterFinish = byteCounter.getFinishTime();
      long totalTime = counterFinish - senderStart;

      System.err.println("Time from sending start to counter finish was " + totalTime);

      assertTrue("Total time from send start to read finish should be greater than 1 minute but was " + totalTime + " milliseconds", totalTime > 60000);
      assertEquals(byteCounter.getByteCount(), TEST_CAPACITY);
   }

   private static class ByteSender extends Thread {

      private final Connection connection;
      private final AtomicLong timeTaken;
      private final AtomicLong startTime;
      private final long duration;
      private final long size;

      public ByteSender(Connection connection, long duration, long size) {
         this.startTime = new AtomicLong();
         this.timeTaken = new AtomicLong();
         this.connection = connection;
         this.duration = duration;
         this.size = size;
      }

      public long getStartTime() {
         return startTime.get();
      }

      public long getTimeTaken() {
         return timeTaken.get();
      }

      public void run() {
         try {
            long beginSending = System.currentTimeMillis();
            startTime.set(beginSending);

            OutputStream output = connection.getOutputStream();
            byte[] data = new byte[1024];
            long blocks = size / data.length;
            long remainder = size % data.length;
            byte[] remainderBlock = new byte[0];

            if (remainder > 0) {
               remainderBlock = new byte[(int) remainder];
            }
            long pause = duration / blocks;
            long totalCount = 0;

            for (long i = 0; i < blocks; i++) {
               long start = System.currentTimeMillis();

               System.err.println("OUT totalCount=" + totalCount + " timeTaken=" + (start - beginSending));
               output.write(data);

               long complete = System.currentTimeMillis();
               long timeTaken = complete - start;

               if (timeTaken < pause && pause > 0) {
                  Thread.sleep(pause - timeTaken);
               }
               totalCount += data.length;
            }
            output.write(remainderBlock);
            timeTaken.set(System.currentTimeMillis() - beginSending);
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            connection.close();
         }
      }

   }

   private static class ByteCounter extends Thread {

      private final Connection connection;
      private final AtomicLong counter;
      private final AtomicLong finishTime;

      public ByteCounter(Connection connection) throws IOException {
         this.finishTime = new AtomicLong();
         this.counter = new AtomicLong();
         this.connection = connection;
      }

      public long getFinishTime() {
         return finishTime.get();
      }

      public long getByteCount() {
         return counter.get();
      }

      public void run() {
         try {
            InputStream input = connection.getInputStream();
            byte[] data = new byte[1024];
            long beginReading = System.currentTimeMillis();

            while (true) {
               long beforeRead = System.currentTimeMillis();
               int count = input.read(data);
               long afterRead = System.currentTimeMillis();

               if (count == -1) {
                  break;
               }
               counter.getAndAdd(count);
               long currentTime = System.currentTimeMillis();
               System.err.println("IN totalCount=" + counter + " timeTaken=" + (currentTime - beginReading) + " thisReadWas=" + (afterRead - beforeRead) + " count=" + count);

            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            finishTime.set(System.currentTimeMillis());
         }
      }

      public void close() {
         connection.close();
      }
   }

   private static class ByteCounterServer implements Runnable {

      private BlockingQueue<ByteCounter> counters;
      private SocketAcceptor acceptor;
      private AtomicBoolean active;
      private Thread thread;

      public ByteCounterServer(int port) throws IOException {
         this.counters = new LinkedBlockingQueue<ByteCounter>();
         this.acceptor = new SocketAcceptor(port);
         this.active = new AtomicBoolean();
      }

      public ByteCounter nextCounter() throws InterruptedException {
         return counters.take();
      }

      public boolean isRunning() {
         return active.get();
      }

      public void start() {
         if (!isRunning()) {
            thread = new Thread(this);
            thread.setName(ByteCounterServer.class.getSimpleName());
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
                  ByteCounter accumulator = new ByteCounter(connection);
                  counters.offer(accumulator);
                  accumulator.start();
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }
}
