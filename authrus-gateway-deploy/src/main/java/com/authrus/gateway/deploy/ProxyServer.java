package com.authrus.gateway.deploy;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import lombok.AllArgsConstructor;

import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.Socket;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.simpleframework.transport.trace.TraceAnalyzer;

import com.zuooh.common.ssl.Certificate;
import com.zuooh.http.proxy.ProxyContainer;

public class ProxyServer {

   private final SocketProcessor processor;
   private final TraceAnalyzer analyzer;
   private final AtomicBoolean authorize;

   public ProxyServer(ProxyContainer proxy, TraceAnalyzer analyzer, int threads) throws IOException {
      this.processor = new ContainerSocketProcessor(proxy, threads);
      this.authorize = new AtomicBoolean(true);
      this.analyzer = analyzer;
   }

   public Connection start(SocketAddress address) throws IOException {
      CertificateProcessor processor = new CertificateProcessor(null);
      SocketConnection connection = new SocketConnection(processor, analyzer);
      
      connection.connect(address);
      return connection;
   }
   
   public Connection start(SocketAddress address, Certificate certificate) throws IOException {
      CertificateProcessor processor = new CertificateProcessor(certificate);
      SocketConnection connection = new SocketConnection(processor, analyzer);
      
      SSLContext context = certificate.getContext();
      connection.connect(address, context);
      return connection;
   }

   @AllArgsConstructor
   private class CertificateProcessor implements SocketProcessor {
   
      private final Certificate certificate;
      
      @Override
      public void process(Socket socket) throws IOException {
         SSLEngine engine = socket.getEngine();
         
         if(certificate != null) {
            String[] suites = certificate.getCipherSuites();
            String[] protocols = certificate.getProtocols();
            
            if(engine != null) {
               boolean request = authorize.get();
               
               if(suites != null && suites.length > 0) {
                  engine.setEnabledCipherSuites(suites);
               }
               if(protocols != null && protocols.length > 0) {
                  engine.setEnabledProtocols(protocols);
               }
               engine.setWantClientAuth(request);
            }
         }
         processor.process(socket);
      }

      @Override
      public void stop() throws IOException {
               
      }
   }
}
