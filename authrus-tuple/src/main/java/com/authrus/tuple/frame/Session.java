package com.authrus.tuple.frame;

/**
 * A session object represents a connection with which messages are
 * sent. A session is typically a TCP connection which can have a 
 * ping message dispatched as a means to keep it alive.
 * 
 * @author Niall Gallagher
 */
public interface Session {
   boolean isOpen();
   void ping();
   void close();
}
