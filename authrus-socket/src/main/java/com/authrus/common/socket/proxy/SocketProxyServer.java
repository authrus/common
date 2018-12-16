package com.authrus.common.socket.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;
import com.authrus.common.socket.Acceptor;
import com.authrus.common.socket.Connection;
import com.authrus.common.socket.Connector;
import com.authrus.common.socket.HostAddress;
import com.authrus.common.thread.ThreadPool;
import com.authrus.common.thread.ThreadPoolFactory;

public class SocketProxyServer {

   private static final Logger LOG = LoggerFactory.getLogger(SocketProxyServer.class);

   private final Cache<String, String> addressResolver;
   private final Set<Connection> currentConnections;
   private final Set<String> blockedAddresses;
   private final ProxyListener proxyListener;
   private final ThreadFactory threadFactory;
   private final Executor threadPool;
   private final Connector connector;
   private final Acceptor acceptor;

   public SocketProxyServer(SocketProxy proxy, Acceptor acceptor, Connector connector) {
      this.addressResolver = new LeastRecentlyUsedCache<String, String>(5000);
      this.threadFactory = new ThreadPoolFactory(ProxyListener.class);
      this.currentConnections = new CopyOnWriteArraySet<Connection>();
      this.blockedAddresses = new CopyOnWriteArraySet<String>();
      this.threadPool = new ThreadPool(threadFactory);
      this.proxyListener = new ProxyListener(proxy);
      this.connector = connector;
      this.acceptor = acceptor;
   }

   public void start() {
      threadPool.execute(proxyListener);
   }

   public void stop() {
      acceptor.close();
      closeAll();
   }

   public String showConnections() {
      return currentConnections.toString();
   }

   public void closeNotConnected() {
      for (Connection connection : currentConnections) {
         if (!connection.isConnected()) {
            connection.close();
         }
      }
   }

   public void closeAll() {
      for (Connection connection : currentConnections) {
         connection.close();
      }
   }

   public void closeAndBlock(String pattern) {
      for (Connection connection : currentConnections) {
         HostAddress address = connection.getRemoteAddress();
         String text = address.toString();

         if (text.matches(pattern)) {
            connection.close();
         }
      }
      blockedAddresses.add(pattern);
   }

   public String resolveAddress(String destination) {
      return addressResolver.fetch(destination);
   }

   public void allowAddress(String pattern) {
      blockedAddresses.remove(pattern);
   }

   public void blockAddress(String pattern) {
      blockedAddresses.add(pattern);
   }

   public boolean isBlocked(String address) {
      for (String blockedAddress : blockedAddresses) {
         if (address.matches(blockedAddress)) {
            return true;
         }
      }
      return false;
   }

   public boolean isBlocked(HostAddress address) {
      String text = address.toString();
      return isBlocked(text);
   }

   private class ProxyListener implements Runnable {

      private final SocketProxy proxy;

      public ProxyListener(SocketProxy proxy) {
         this.proxy = proxy;
      }

      @Override
      public void run() {
         while (acceptor.isConnected()) {
            try {
               Connection source = accept();

               try {
                  Connection destination = connect();
                  HostAddress remote = source.getRemoteAddress();
                  HostAddress local = destination.getLocalAddress();
                  String from = remote.toString();
                  String to = local.toString();

                  addressResolver.cache(to, from);
                  currentConnections.add(source);
                  proxy.connect(source, destination);
               } catch (Exception e) {
                  LOG.info("Could not connect so dropping connection", e);

                  if (source.isConnected()) {
                     source.close();
                  }
               }
            } catch (Exception e) {
               LOG.info("Could not accept connection", e);
            }
         }
      }

      private Connection connect() throws IOException {
         return connector.connect();
      }

      private Connection accept() throws IOException {
         while (true) {
            Connection source = acceptor.accept();
            HostAddress address = source.getRemoteAddress();

            if (!isBlocked(address)) {
               return new ProxyConnection(source);
            } else {
               source.close();
            }
         }
      }
   }

   private class ProxyConnection implements Connection {

      private final Connection connection;

      public ProxyConnection(Connection connection) {
         this.connection = connection;
      }

      @Override
      public HostAddress getRemoteAddress() {
         return connection.getRemoteAddress();
      }

      @Override
      public HostAddress getLocalAddress() {
         return connection.getLocalAddress();
      }

      @Override
      public InputStream getInputStream() throws IOException {
         return connection.getInputStream();
      }

      @Override
      public OutputStream getOutputStream() throws IOException {
         return connection.getOutputStream();
      }

      @Override
      public boolean isReadFailure() {
         return connection.isReadFailure();
      }

      @Override
      public boolean isWriteFailure() {
         return connection.isWriteFailure();
      }

      @Override
      public boolean isConnected() {
         return connection.isConnected();
      }

      @Override
      public boolean close() {
         currentConnections.remove(this);
         return connection.close();
      }

      @Override
      public String toString() {
         return connection.toString();
      }
   }
}
