package com.authrus.tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import junit.framework.TestCase;

import org.simpleframework.transport.Certificate;
import org.simpleframework.transport.Channel;
import org.simpleframework.transport.Transport;
import org.simpleframework.transport.TransportChannel;
import org.simpleframework.transport.reactor.SynchronousReactor;
import org.simpleframework.transport.trace.Trace;

import com.authrus.tuple.frame.Frame;
import com.authrus.tuple.frame.FrameCollector;
import com.authrus.tuple.frame.FrameEncoder;
import com.authrus.tuple.frame.FrameListener;
import com.authrus.tuple.frame.FrameType;
import com.authrus.tuple.frame.Sequence;

public class FrameEncoderTest extends TestCase {
   
   public void testEncoder() throws Exception {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      Transport transport = new StreamTransport(System.in, buffer);
      Channel channel = new TransportChannel(transport);
      FrameEncoder encoder = new FrameEncoder(channel, 10);      
      StringBuilder builder = new StringBuilder();
      
      for(int i = 0; i < 100; i++) {
         builder.append("message-").append(i).append(",");
      }
      byte[] array = builder.toString().getBytes();
      ByteBuffer data = ByteBuffer.wrap(array);
      Frame frame = new Frame(FrameType.MESSAGE, data);
      
      encoder.encode(frame);
      
      byte[] result = buffer.toByteArray();
      ByteArrayInputStream source = new ByteArrayInputStream(result);
      Transport sourceTransport = new StreamTransport(source, System.err);
      Channel sourceChannel = new TransportChannel(sourceTransport);
      FrameAdapter listener = new FrameAdapter();
      SynchronousReactor reactor = new SynchronousReactor();
      FrameCollector collector = new FrameCollector(listener, null, null, sourceChannel, reactor);
      
      collector.run();
      
      assertNotNull(listener.frame);
      assertEquals(listener.frame.getType(), FrameType.MESSAGE);
      assertEquals(listener.frame.getSize(), array.length);
      assertEquals(listener.frame.getData().order(), data.order()); // maintains byte order info
      
      data = listener.frame.getData();
      String text = encode(data);
      
      assertEquals(text, builder.toString());
   }
   
   class FrameAdapter implements FrameListener {
      
      public Frame frame;

      @Override
      public void onFrame(Frame frame) {
         this.frame = frame;
      }

      @Override
      public void onConnect() {  
      }
      
      @Override
      public void onException(Exception cause) {
      }      

      @Override
      public void onSuccess(Sequence sequence) {  
      }      

      @Override
      public void onHeartbeat() {  
      }

      @Override
      public void onClose() { 
      }
      
   }
   
   class StreamTransport implements Transport {
      
      private final WritableByteChannel write;
      private final ReadableByteChannel read;
      private final OutputStream out;
      
      public StreamTransport(InputStream in, OutputStream out) {
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
         return null;
      }
   }
      
  public String encode(ByteBuffer buffer) throws IOException {
     return encode(buffer, "UTF-8"); 
  }
     
  public String encode(ByteBuffer buffer, String encoding) throws IOException {
     Charset charset = Charset.forName(encoding); 
     CharBuffer text = charset.decode(buffer);

     return text.toString();
  }
}
