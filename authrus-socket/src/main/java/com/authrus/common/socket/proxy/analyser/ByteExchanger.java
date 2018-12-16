package com.authrus.common.socket.proxy.analyser;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.authrus.common.socket.Connection;
import com.authrus.common.socket.HostAddress;
import com.authrus.common.time.Clock;
import com.authrus.common.time.SystemClock;
import com.authrus.common.time.Time;

/**
 * A byte exchanger acts as a pipe transferring an input stream to an 
 * output stream asynchronously. Each packet exchanged results in an 
 * update to the {@link PacketAnalyser} which can record the traffic 
 * and perform performance measurements such as latency and throughput.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.common.socket.proxy.analyser.AnalyserProxy
 */
public class ByteExchanger implements Runnable {

   private static final Logger LOG = LoggerFactory.getLogger(ByteExchanger.class);

   private final ConnectionMonitor monitor;
   private final PacketAnalyser analyser;
   private final ConnectionContext context;
   private final Connection destination;
   private final Connection source;
   private final Clock clock;

   public ByteExchanger(PacketAnalyser analyser, ConnectionMonitor monitor, Connection source, Connection destination) {
      this.context = new ConnectionContext(source, destination);
      this.clock = new SystemClock();
      this.destination = destination;
      this.analyser = analyser;
      this.monitor = monitor;
      this.source = source;
   }

   @Override
   public void run() {
      try {
         renameThread();
         startExchange();
         exchangeBytes();
      } catch (Throwable cause) {
         reportFailure(cause);
      } finally {
         closeConnections();
         stopExchange();
      }
   }

   private void startExchange() {
      if (monitor != null) {
         try {
            monitor.onOpen(context);
         } catch (Throwable t) {
            LOG.info("Error on start notification", t);
         }
      }
   }

   private void stopExchange() {
      if (monitor != null) {
         try {
            monitor.onClose(context);
         } catch (Throwable t) {
            LOG.info("Error on start notification", t);
         }
      }
   }

   private void exchangeBytes() throws Exception {
      InputStream sourceStream = source.getInputStream();
      OutputStream destinationStream = destination.getOutputStream();
      String connection = hostDetails();
      byte[] buffer = new byte[2048];

      while (true) {
         Time startTime = clock.currentTime();
         int count = sourceStream.read(buffer);

         try {
            if (count != -1) {
               destinationStream.write(buffer, 0, count);
            } else {
               break;
            }
         } finally {
            Time finishTime = clock.currentTime();

            if (analyser != null) {
               Packet packet = new Packet(connection, buffer, 0, count);

               try {
                  analyser.analyse(startTime, finishTime, packet);
               } finally {
                  packet.dispose();
               }
            }
         }
      }
      LOG.info("Finished reading from source '" + source + "'");
   }

   private void reportFailure(Throwable cause) {
      if (destination.isWriteFailure()) {
         LOG.info("Error writing to destination '" + destination + "'");
      }
      if (source.isReadFailure()) {
         LOG.info("Error reading from source '" + source + "'");
      }
      LOG.info("Error during byte exchange", cause);

      if (monitor != null) {
         try {
            monitor.onError(context, cause);
         } catch (Throwable t) {
            LOG.error("Error handling exception", t);
         }
      }
   }

   private void closeConnections() {
      close(source);
      close(destination);
   }

   private void renameThread() {
      Thread exchangeThread = Thread.currentThread();
      String connection = connectionDetails();

      exchangeThread.setName(connection);
   }

   private String hostDetails() {
      HostAddress fromAddress = source.getRemoteAddress();
      HostAddress toAddress = destination.getRemoteAddress();
      String fromHost = fromAddress.getHost();
      String toHost = toAddress.getHost();

      return String.format("%s -> %s", fromHost, toHost);
   }

   private String connectionDetails() {
      HostAddress fromAddress = source.getRemoteAddress();
      HostAddress toAddress = destination.getRemoteAddress();

      return String.format("%s -> %s", fromAddress, toAddress);
   }

   private void close(Connection connection) {
      try {
         connection.close();
      } catch (Exception e) {
         LOG.info("Could not close connection", e);
      }
   }
}
