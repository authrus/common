package com.authrus.common.socket.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import com.authrus.common.ssl.Certificate;

public class SecureSocketBuilder {

   private final SSLContext context;
   private final String[] protocols;
   private final String[] suites;

   public SecureSocketBuilder(Certificate certificate) {
      this.protocols = certificate.getCipherSuites();
      this.suites = certificate.getProtocols();
      this.context = certificate.getContext();
   }
   
   public Socket createSocket(String host, int port) throws IOException {
      SocketFactory factory = context.getSocketFactory();
      Socket socket = factory.createSocket(host, port);
      
      if(protocols != null || suites != null) {
         SSLSocket configuration = (SSLSocket)socket;
         
         if(protocols != null && protocols.length > 0) {
            configuration.setEnabledProtocols(protocols);
         }
         if(suites != null && suites.length > 0) {
            configuration.setEnabledCipherSuites(suites);
         }
      }
      return socket;
   }
   
   public ServerSocket createServerSocket(int port) throws IOException {
      ServerSocketFactory factory = context.getServerSocketFactory();
      ServerSocket socket = factory.createServerSocket(port);
      
      if(protocols != null || suites != null) {
         SSLServerSocket configuration = (SSLServerSocket)socket;
         
         if(protocols != null && protocols.length > 0) {
            configuration.setEnabledProtocols(protocols);
         } 
         if(suites != null && suites.length > 0) {
            configuration.setEnabledCipherSuites(suites);
         }
      }
      return socket;
   }
}
