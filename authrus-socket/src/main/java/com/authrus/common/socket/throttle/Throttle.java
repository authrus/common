package com.authrus.common.socket.throttle;

public interface Throttle {
   ThrottleResult update(long packetSize);
}
