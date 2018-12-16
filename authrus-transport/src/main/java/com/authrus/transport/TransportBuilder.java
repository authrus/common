package com.authrus.transport;

import java.io.IOException;

import org.simpleframework.transport.Transport;

public interface TransportBuilder {
   Transport connect() throws IOException;
}
