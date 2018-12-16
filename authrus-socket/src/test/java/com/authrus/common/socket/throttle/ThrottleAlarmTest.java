package com.authrus.common.socket.throttle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;

public class ThrottleAlarmTest extends TestCase {

   public void testThrottleAlarmCreepUpOnAlertThreshold() throws Exception {
      byte[] chunk = new byte[20000];
      byte[] buffer = new byte[20000];
      Arrays.fill(chunk, (byte) 0xff);
      MockAlarm alarm = new MockAlarm();
      ByteArrayInputStream input = new ByteArrayInputStream(chunk);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      MockConnection connection = new MockConnection(input, output);
      ThrottleCapacity capacity = new ThrottleCapacity(1000, 90);
      ThrottleMonitor monitor = new ThrottleMonitor(capacity, capacity);
      ThrottleConnection throttleConnection = new ThrottleConnection(monitor, alarm, connection);
      InputStream throttleInput = throttleConnection.getInputStream();

      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 100
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 200
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 300
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 400
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 500
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 600
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 700
      assertEquals(alarm.getInputCount(), 0);
      assertEquals(throttleInput.read(buffer, 0, 100), 100); // 800
      assertEquals(alarm.getInputCount(), 1);

      ThrottleEvent event = alarm.nextInputEvent();

      assertEquals(event.getPacketSize(), 100);
      assertEquals(event.getThrottleResult().getAlertPercentage(), 75);
      assertEquals(event.getThrottleResult().getTotalCapacity(), 1000);
      assertEquals(event.getThrottleResult().getFreeCapacity(), 900);
   }

   /*
    * public void testThrottleAlarmExhaustTotal() throws Exception { byte[]
    * chunk = new byte[60000]; byte[] buffer = new byte[60000];
    * Arrays.fill(chunk, (byte)0xff); MockAlarm alarm = new MockAlarm();
    * ByteArrayInputStream input = new ByteArrayInputStream(chunk);
    * ByteArrayOutputStream output = new ByteArrayOutputStream(); MockConnection
    * connection = new MockConnection(input, output); ThrottleCapacity capacity
    * = new ThrottleCapacity(60000, 90); ThrottleMonitor monitor = new
    * ThrottleMonitor(capacity, capacity); ThrottleConnection throttleConnection
    * = new ThrottleConnection(monitor, alarm, connection); InputStream
    * throttleInput = throttleConnection.getInputStream();
    * 
    * int count = throttleInput.read(buffer, 0, 60000);
    * 
    * assertEquals(count, 1000); assertEquals(alarm.getInputCount(), 1);
    * 
    * ThrottleEvent event = alarm.nextInputEvent();
    * 
    * assertEquals(event.getPacketSize(), 1000);
    * assertEquals(event.getThrottleResult().getAlertPercentage(), 75.0);
    * assertEquals(event.getThrottleResult().getTotalCapacity(), 1000);
    * assertEquals(event.getThrottleResult().getFreeCapacity(), 900); }
    */

   private static class MockAlarm implements ThrottleAlarm {

      private final BlockingQueue<ThrottleEvent> input;
      private final BlockingQueue<ThrottleEvent> output;

      public MockAlarm() {
         this.input = new LinkedBlockingQueue<ThrottleEvent>();
         this.output = new LinkedBlockingQueue<ThrottleEvent>();
      }

      public int getInputCount() {
         return input.size();
      }

      public int getOutputCount() {
         return output.size();
      }

      public ThrottleEvent nextOutputEvent() throws Exception {
         return output.take();
      }

      public ThrottleEvent nextInputEvent() throws Exception {
         return input.take();
      }

      @Override
      public void raiseInputAlarm(ThrottleEvent event) {
         input.offer(event);
      }

      @Override
      public void raiseOutputAlarm(ThrottleEvent event) {
         output.offer(event);
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
