package com.authrus.common.socket.proxy.analyser;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombinationMonitor implements ConnectionMonitor {

   private static final Logger LOG = LoggerFactory.getLogger(CombinationMonitor.class);

   private final List<ConnectionMonitor> monitors;

   public CombinationMonitor(List<ConnectionMonitor> monitors) {
      this.monitors = monitors;
   }

   @Override
   public void onOpen(ConnectionContext context) {
      for (ConnectionMonitor monitor : monitors) {
         try {
            monitor.onOpen(context);
         } catch (Exception e) {
            LOG.info("Could not notify of connection open", e);
         }
      }
   }

   @Override
   public void onClose(ConnectionContext context) {
      for (ConnectionMonitor monitor : monitors) {
         try {
            monitor.onClose(context);
         } catch (Exception e) {
            LOG.info("Could not notify of connection close", e);
         }
      }
   }

   @Override
   public void onError(ConnectionContext context, Throwable cause) {
      for (ConnectionMonitor monitor : monitors) {
         try {
            monitor.onError(context, cause);
         } catch (Exception e) {
            LOG.info("Could not notify of connection error", e);
         }
      }
   }
}
