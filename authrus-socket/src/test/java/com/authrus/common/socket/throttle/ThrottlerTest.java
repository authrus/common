package com.authrus.common.socket.throttle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class ThrottlerTest extends TestCase {

   private static final int PAYLOAD_SIZE = 1024 * 10;

   public void testThrottle() throws Exception {
      ThrottleCapacity writeCapacity = new ThrottleCapacity(60 * 1024);
      ThrottleCapacity readCapacity = new ThrottleCapacity(60 * 2048);
      ThrottleRegistry writeRegistry = new ThrottleRegistry(writeCapacity);
      ThrottleRegistry readRegistry = new ThrottleRegistry(readCapacity);
      ThrottleAlarm throttleAlarm = new MockAlarm();
      Throttler throttler = new Throttler(readRegistry, writeRegistry, throttleAlarm);

      assertEquals(writeCapacity.getTotalBytes(), 60 * 1024);
      assertEquals(writeCapacity.getFreePercentage(), 90);
      assertEquals(writeCapacity.getAlertPercentage(), 75);
      assertEquals(readCapacity.getTotalBytes(), 60 * 2048);
      assertEquals(readCapacity.getFreePercentage(), 90);
      assertEquals(readCapacity.getAlertPercentage(), 75);
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < 1000; i++) {
         builder.append("i=[").append(i).append("]").append("\n");
      }
      String text = builder.toString();
      byte[] data = text.getBytes("UTF-8");
      int length = data.length;
      ByteArrayInputStream input = new ByteArrayInputStream(data);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      MockConnection originalConnection = new MockConnection(input, output);
      Connection throttledConnection = throttler.throttle(originalConnection);
      InputStream throttledInput = throttledConnection.getInputStream();
      long startTime = System.currentTimeMillis();
      byte[] buffer = new byte[1024];
      long amountSent = 0;
      int count = 0;

      while ((count = throttledInput.read(buffer)) != -1) {
         long timeElapsed = System.currentTimeMillis() - startTime;
         amountSent += count;
         System.err.write(buffer, 0, count);
         System.err.println();
         System.err.println("READ timeElapsed=" + timeElapsed + " chunksSent=" + (amountSent / 1024) + " amountSent=" + amountSent);
         output.write(buffer, 0, count);
      }
      assertEquals(output.toByteArray().length, length);

      OutputStream throttledOutput = throttledConnection.getOutputStream();
      input = new ByteArrayInputStream(data);
      output.reset();

      while ((count = input.read(buffer)) != -1) {
         long timeElapsed = System.currentTimeMillis() - startTime;
         amountSent += count;
         System.err.write(buffer, 0, count);
         System.err.println();
         System.err.println("WRITE timeElapsed=" + timeElapsed + " chunksSent=" + (amountSent / 1024) + " amountSent=" + amountSent);
         throttledOutput.write(buffer, 0, count);
      }
      assertEquals(output.toByteArray().length, length);

   }

   private static class MockAlarm implements ThrottleAlarm {

      @Override
      public void raiseInputAlarm(ThrottleEvent throttleEvent) {
         System.err.printf("INPUT %s -> %s [%s]", throttleEvent.getRemoteAddress(), throttleEvent.getLocalAddress(), throttleEvent.getThrottleResult().getThrottleDelay());
      }

      @Override
      public void raiseOutputAlarm(ThrottleEvent throttleEvent) {
         System.err.printf("OUTPUT %s -> %s [%s]", throttleEvent.getRemoteAddress(), throttleEvent.getLocalAddress(), throttleEvent.getThrottleResult().getThrottleDelay());
      }
   }

   private static class MockConnection implements Connection {

      private final OutputStream output;
      private final InputStream input;

      public MockConnection(InputStream input, OutputStream output) {
         this.output = output;
         this.input = input;
      }

      @Override
      public HostAddress getLocalAddress() {
         return new HostAddress("local", -1);
      }

      @Override
      public HostAddress getRemoteAddress() {
         return new HostAddress("remote", -1);
      }

      @Override
      public InputStream getInputStream() throws IOException {
         return input;
      }

      @Override
      public OutputStream getOutputStream() throws IOException {
         return output;
      }

      @Override
      public boolean isReadFailure() {
         return false;
      }

      @Override
      public boolean isWriteFailure() {
         return false;
      }

      @Override
      public boolean isConnected() {
         return false;
      }

      @Override
      public boolean close() {
         return true;
      }
   }
}
