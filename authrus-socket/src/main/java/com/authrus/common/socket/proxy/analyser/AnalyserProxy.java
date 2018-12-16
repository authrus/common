package com.authrus.common.socket.proxy.analyser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.proxy.SocketProxy;

public class AnalyserProxy implements SocketProxy {

   private final PacketAnalyser inputAnalyser;
   private final PacketAnalyser outputAnalyser;
   private final Executor executor;
   private final ConnectionMonitor monitor;

   public AnalyserProxy(PacketAnalyser inputAnalyser, PacketAnalyser outputAnalyser) {
      this(inputAnalyser, outputAnalyser, null);
   }

   public AnalyserProxy(PacketAnalyser inputAnalyser, PacketAnalyser outputAnalyser, ConnectionMonitor monitor) {
      this.executor = Executors.newCachedThreadPool();
      this.inputAnalyser = inputAnalyser;
      this.outputAnalyser = outputAnalyser;
      this.monitor = monitor;
   }

   @Override
   public void connect(Connection source, Connection destination) {
      ByteExchanger sourceDestination = new ByteExchanger(inputAnalyser, monitor, source, destination);
      ByteExchanger destinationSource = new ByteExchanger(outputAnalyser, monitor, destination, source);

      executor.execute(sourceDestination);
      executor.execute(destinationSource);
   }
}
