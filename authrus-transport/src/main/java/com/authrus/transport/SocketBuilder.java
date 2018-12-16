package com.authrus.transport;

import java.io.IOException;

import javax.net.ssl.SSLEngine;

import org.simpleframework.transport.Socket;

public interface SocketBuilder {
   Socket connect() throws IOException;
   Socket connect(SSLEngine engine) throws IOException;
}
