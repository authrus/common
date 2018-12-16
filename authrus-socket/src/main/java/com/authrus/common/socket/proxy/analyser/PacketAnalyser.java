package com.authrus.common.socket.proxy.analyser;

import com.authrus.common.time.Time;

/**
 * An analyser is used to collect information about a connection. When 
 * data is collected performance measurements can be made, such as the 
 * latency of the connection or the throughput.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.common.socket.proxy.analyser.AnalyserProxy
 */
public interface PacketAnalyser {
   void analyse(Time startTime, Time endTime, Packet packetData);
}
