package com.authrus.common.socket.proxy.analyser;

import java.util.Collections;
import java.util.List;

import com.authrus.common.time.Time;

public class CombinationAnalyser implements PacketAnalyser {

   private final List<PacketAnalyser> analysers;

   public CombinationAnalyser() {
      this(Collections.EMPTY_LIST);
   }
   
   public CombinationAnalyser(List<PacketAnalyser> analysers) {
      this.analysers = analysers;
   }

   @Override
   public void analyse(Time startTime, Time endTime, Packet packetData) {
      for (PacketAnalyser analyser : analysers) {
         analyser.analyse(startTime, endTime, packetData);
      }
   }
}
