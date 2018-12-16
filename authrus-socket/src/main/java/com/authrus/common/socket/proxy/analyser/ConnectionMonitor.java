package com.authrus.common.socket.proxy.analyser;

public interface ConnectionMonitor {
   void onOpen(ConnectionContext context);
   void onClose(ConnectionContext context);
   void onError(ConnectionContext context, Throwable cause);
}
