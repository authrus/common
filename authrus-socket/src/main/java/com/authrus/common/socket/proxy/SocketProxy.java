package com.authrus.common.socket.proxy;

import com.authrus.common.socket.Connection;

/**
 * A proxy is used to act as a proxy between two {@link Connection} 
 * objects. It must exchange bytes between the connections and if 
 * desired perform some form of analysis or manipulation of data.
 * 
 * @author Niall Gallagher
 */
public interface SocketProxy {
   void connect(Connection source, Connection destination);
}
