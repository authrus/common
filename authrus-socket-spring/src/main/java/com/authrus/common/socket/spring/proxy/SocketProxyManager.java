package com.authrus.common.socket.spring.proxy;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.authrus.common.socket.proxy.SocketProxyServer;

@ManagedResource(description="Manage proxy TCP connections")
public class SocketProxyManager {

   private final SocketProxyServer server;

   public SocketProxyManager(SocketProxyServer server) {
      this.server = server;
   }

   @ManagedOperation(description="Start the proxy server")
   public void start() {
      server.start();
   }

   @ManagedOperation(description="Stop the proxy server")
   public void stop() {     
      server.stop();
   }

   @ManagedOperation(description="Shows current connections")
   public String showConnections() {
      return server.showConnections();
   }

   @ManagedOperation(description="Close any connections not connected")
   public void closeNotConnected() {
      server.closeNotConnected();
   }

   @ManagedOperation(description="Close all connections")
   public void closeAll() {
      server.closeAll();
   }

   @ManagedOperation(description="Closes any address that matches")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name = "pattern", description = "Regular expression") 
   })
   public void closeAndBlock(String pattern) {
      server.closeAndBlock(pattern);
   }

   @ManagedOperation(description="Resolve source address from destination address")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="destination", description="Destination address") 
   })
   public String resolveAddress(String destination) {
      return server.resolveAddress(destination);
   }

   @ManagedOperation(description="Removes address from blocked list")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="pattern", description="Regular expression") 
   })
   public void allowAddress(String pattern) {
      server.allowAddress(pattern);
   }

   @ManagedOperation(description="Adds address to blocked list")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="pattern", description="Regular expression") 
   })
   public void blockAddress(String pattern) {
      server.blockAddress(pattern);
   }

   @ManagedOperation(description="Check if an address is blocked")
   @ManagedOperationParameters({ 
      @ManagedOperationParameter(name="address", description="Address to match") 
   })
   public boolean isBlocked(String address) {
      return server.isBlocked(address);
   }
}
