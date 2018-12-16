package com.authrus.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketWrapper;
import org.simpleframework.transport.trace.Trace;
import org.simpleframework.transport.trace.TraceAnalyzer;

public class DirectSocketBuilder implements SocketBuilder {
   
   private final TraceAnalyzer analyzer;
   private final SocketAddress address;
   private final String host;
   private final int port;
   
   public DirectSocketBuilder(TraceAnalyzer analyzer, String host, int port) {
      this.address = new InetSocketAddress(host, port);
      this.analyzer = analyzer;
      this.host = host;
      this.port = port;
   }
   
   public Socket connect() throws IOException {
      return connect(null);
   }
   
   public Socket connect(SSLEngine engine) throws IOException {
      SocketChannel channel = SocketChannel.open();
      Trace trace = analyzer.attach(channel);
      
      channel.socket().setTcpNoDelay(true);
      channel.configureBlocking(false);
      channel.connect(address);
      
      return new SocketWrapper(channel, trace, engine);   
   }
   
   @Override
   public String toString() {
      return String.format("%s:%s", host, port);
   } 
}
