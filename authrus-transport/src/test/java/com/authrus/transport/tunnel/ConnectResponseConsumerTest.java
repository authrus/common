package com.authrus.transport.tunnel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import junit.framework.TestCase;

import org.simpleframework.transport.ByteCursor;
import org.simpleframework.transport.Certificate;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.trace.Trace;

import com.authrus.transport.tunnel.ConnectResponseConsumer;
import com.authrus.transport.tunnel.TunnelState;

public class ConnectResponseConsumerTest extends TestCase {
   
   public static final byte[] HEADER = (
         "HTTP/1.1 200 OK\r\n"+               
         "Server : test/1.0  \r\n"+
         "Connection: keep-alive\r\n"+
         "\r\n"
         ).getBytes();
   
   public void testResponseConsumer() throws Exception {
      ByteArrayInputStream in = new ByteArrayInputStream(HEADER);
      MockTransport transport = new MockTransport(in, System.out);
      Channel channel = new TransportChannel(transport);
      ByteCursor cursor = channel.getCursor();
      ConnectResponseConsumer consumer = new ConnectResponseConsumer();
      while(!consumer.isFinished()) {
         consumer.consume(cursor);
      }
      System.err.println(consumer.getHeader());
      assertEquals("200", consumer.getStatus());
      assertEquals(TunnelState.ESTABLISHED, consumer.getState());
   }

   private class MockTransport implements Transport {
      
      private final WritableByteChannel write;
      private final ReadableByteChannel read;
      private final OutputStream out;
      
      public MockTransport(InputStream in, OutputStream out) {
         this.write = Channels.newChannel(out);
         this.read = Channels.newChannel(in);
         this.out = out;
      }

      public void close() throws IOException {
         write.close();
         read.close();
      }

      public void flush() throws IOException {
         out.flush();
      }

      public int read(ByteBuffer buffer) throws IOException {
         return read.read(buffer);
      }

      public void write(ByteBuffer buffer) throws IOException {
         write.write(buffer);
      }

      public Map getAttributes() {
         return null;
      }

      public SocketChannel getChannel() {
         return null;
      }   

      public SSLEngine getEngine() {
         return null;
      }

      public Certificate getCertificate() {
         return null;
      }

      public Trace getTrace() {
         return new MockTrace();
      }
   }
   
   public class MockTrace implements Trace{
      public void trace(Object event) {
         trace(event, null);
      }
      public void trace(Object event, Object value) {
         System.err.println(event);
      }
   }
}
