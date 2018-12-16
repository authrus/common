package com.authrus.transport.tunnel;

public interface ConnectResponse {
   String getHeader();
   TunnelState getState();
}
